package uk.co.bjdavies.core;

import com.github.zafarkhaja.semver.Version;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.discord.IDiscordFacade;

import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        this.service = Executors.newFixedThreadPool(2, new ThreadFactoryBuilder().setNameFormat("announcement-thread-%d").build());
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
                        String versionName = first.tag_name.toLowerCase().replace("v", "");
                        Version tagVersion = Version.valueOf(versionName);
                        if (tagVersion.greaterThan(currentVersion)) {
                            if (application.getConfig().getSystemConfig().isAutoUpdateOn()) {
                                log.info("Updating....");
                                application.get(UpdateService.class).updateTo(first).subscribe((b) -> {

                                }, (t) -> log.error("Error", t), () -> {
                                    currentVersion = tagVersion;

                                    sendMessage("New server update to: " + versionName,
                                            "Server has updated please use: " +
                                                    application.getConfig().getDiscordConfig().getCommandPrefix() + "restart to update now, or the bot will restart automatically in 10 minutes...");

                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            sendMessage("Bot is now restarting for the update...");
                                            application.restart();
                                        }
                                    }, 1000 * 60 * 10);

                                });
                            }
                        } else {
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
        AnnouncementChannel.all().stream().map(a -> (AnnouncementChannel) a).forEach(a ->
                facade.getClient().getGuildById(Snowflake.of(a.getGuildId())).subscribe(g ->
                        g.getChannelById(Snowflake.of(a.getChannelId())).map(c -> (TextChannel) c)
                                .subscribe(c -> c.createEmbed(spec -> {
                                    spec.setFooter("Server Version: " + application.getServerVersion(), null);
                                    spec.setAuthor("BabbleBot", "https://github.com/bendavies99/BabbleBot-Server", null);
                                    spec.setTimestamp(Instant.now());
                                    facade.getClient().getSelf()
                                            .subscribe(u -> u.asMember(g.getId())
                                                    .subscribe(mem -> mem.getColor()
                                                            .subscribe(spec::setColor)));
                                    spec.setTitle(title);
                                    spec.setDescription("```\n" + message + "```");
                                }).subscribe())));
    }

    public synchronized void sendMessage(String message) {
        sendMessage("New announcement", message);
    }

    public static class TagItem {
        public String tag_name;
        public List<Asset> assets;
    }

    public static class Asset {
        public String browser_download_url;
    }
}
