/*
 * MIT License
 *
 * Copyright (c) 2020 Ben Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package uk.co.bjdavies.core;

import com.github.zafarkhaja.semver.Version;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;
import net.bdavies.api.IApplication;
import net.bdavies.api.discord.IDiscordFacade;

import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
public class AnnouncementService {

    private final IDiscordFacade facade;

    private final IApplication application;
    private final ExecutorService service;
    private final Timer timer;
    private Version currentVersion;

    @Inject
    public AnnouncementService(IDiscordFacade facade, IApplication application) {
        this.facade = facade;
        this.application = application;
        this.service = Executors.newFixedThreadPool(2,
          new ThreadFactoryBuilder().setNameFormat("announcement-thread-%d").build());
        timer = new Timer();
        currentVersion = Version.valueOf(application.getServerVersion());
    }

    public synchronized void stop() {
        timer.cancel();
        this.service.shutdown();
    }

    public synchronized void start() {

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                log.info("Active Threads: " + Thread.activeCount());
            }
        }, 1000, 1000 * 60);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                service.submit(() -> {
                    try {
                        String response =
                          HttpClient.create()
                            .get()
                            .uri("https://api.github.com/repos/bendavies99/Babblebot-Server/releases")
                            .responseContent()
                            .aggregate()
                            .asString()
                            .block();

                        Gson gson = new GsonBuilder().create();

                        List<TagItem> res = gson.fromJson(response, new TypeToken<List<TagItem>>() {
                        }.getType());
                        assert res != null;
                        TagItem first = res.get(0);
                        int index = 0;
                        while (first.prerelease) {
                            first = res.get(++index);
                        }
                        String versionName = first.tag_name.toLowerCase().replace("v", "");
                        Version tagVersion = Version.valueOf(versionName);
                        if (tagVersion.greaterThan(currentVersion)) {
                            if (application.getConfig().getSystemConfig().isAutoUpdateOn()) {
                                log.info("Updating....");
                                application.get(UpdateService.class).updateTo(first).subscribe((b) -> {

                                }, (t) -> log.error("Error", t), () -> {
                                    currentVersion = tagVersion;

                                    sendMessage("New server update to: " + versionName,
                                      "Server has been updated please use: " +
                                        application.getConfig().getDiscordConfig().getCommandPrefix() +
                                        "restart to update now, or the bot will restart automatically in 10 minutes.." +
                                        ".");

                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            sendMessage("Bot is now restarting for the update...");
                                            application.restart();
                                        }
                                    }, 1000 * 60 * 10);

                                });
                            }
                        }
                        else {
                            log.info("Up to date");
                        }
                    } catch (Exception e) {
                        log.error("Checking for update failed.... Probably a rate limit.. please wait an hour.");
                    }
                });
            }
        }, 3000, 1000 * 60 * 60);
    }

    public synchronized void sendMessage(String title, String message) {

        BiConsumer<EmbedCreateSpec, Guild> specConsumer = (spec, g) -> {
            spec.setFooter("Server Version: " + application.getServerVersion(), null);
            spec.setAuthor("BabbleBot", "https://github.com/bendavies99/BabbleBot-Server", null);
            spec.setTimestamp(Instant.now());
            facade.getClient().getSelf()
              .subscribe(u -> u.asMember(g.getId())
                .subscribe(mem -> mem.getColor()
                  .subscribe(spec::setColor)));
            spec.setTitle(title);
            spec.setDescription("```\n" + message + "```");
        };

        if (AnnouncementChannel.all().size() == 0) {
            log.info("Announcement channels are empty so choosing a channel by default use register-ac on a guild to " +
              "set announcement channel");
            facade.getClient().getGuilds().subscribe(g -> {
                g.getChannels().filter(c -> {
                    try {
                        TextChannel channel = (TextChannel) c;
                        return channel != null;
                    } catch (ClassCastException e) {
                        return false;
                    }
                }).map(c -> (TextChannel) c).take(1).subscribe(
                  c -> c.createEmbed(spec -> specConsumer.accept(spec, g)).subscribe());
            });
            return;
        }

        AnnouncementChannel.all().stream().map(a -> (AnnouncementChannel) a).forEach(a ->
          facade.getClient().getGuildById(Snowflake.of(a.getGuildId())).subscribe(g ->
            g.getChannelById(Snowflake.of(a.getChannelId())).map(c -> (TextChannel) c)
              .subscribe(c -> c.createEmbed(spec -> specConsumer.accept(spec, g)).subscribe())));
    }

    public synchronized void sendMessage(String message) {
        sendMessage("New announcement", message);
    }

    public static class TagItem {
        public String tag_name;
        public List<Asset> assets;
        public boolean prerelease;
    }

    public static class Asset {
        public String browser_download_url;
    }
}
