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

package net.bdavies.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zafarkhaja.semver.Version;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.discord.IDiscordFacade;
import net.bdavies.core.repository.AnnouncementChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.netty.http.client.HttpClient;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
@Service
public class AnnouncementService
{
    private final IDiscordFacade facade;

    private final IApplication application;
    private final ExecutorService service;
    private final Timer timer;
    private Version currentVersion;

    private final AnnouncementChannelRepository announcementChannelRepo;

    @Autowired
    public AnnouncementService(IDiscordFacade facade, IApplication application,
                               AnnouncementChannelRepository announcementChannelRepo)
    {
        this.facade = facade;
        this.application = application;
        this.announcementChannelRepo = announcementChannelRepo;
        this.service = Executors.newFixedThreadPool(2);
        timer = new Timer();
        currentVersion = Version.valueOf(application.getServerVersion());
    }

    public synchronized void stop()
    {
        timer.cancel();
        this.service.shutdown();
    }

    public synchronized void start()
    {

        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                service.submit(() -> {
                    try
                    {
                        String response =
                                HttpClient.create()
                                        .get()
                                        .uri("https://api.github.com/repos/bendavies99/Babblebot-Server" +
                                                "/releases")
                                        .responseContent()
                                        .aggregate()
                                        .asString()
                                        .block();

                        ObjectMapper mapper = new ObjectMapper();

                        List<TagItem> res = mapper.readValue(response, new TypeReference<>()
                        {
                        });
                        assert res != null;
                        TagItem first = res.get(0);
                        int index = 0;
                        while (first.prerelease)
                        {
                            first = res.get(++index);
                        }
                        String versionName = first.tag_name.toLowerCase(Locale.ROOT).replace("v", "");
                        Version tagVersion = Version.valueOf(versionName);
                        if (tagVersion.greaterThan(currentVersion) &&
                                (tagVersion.getMajorVersion() == currentVersion.getMajorVersion() &&
                                        !application.hasArgument("--updateMajor")))
                        {
                            if (application.getConfig().getSystemConfig().isAutoUpdateOn())
                            {
                                log.info("Updating....");
                                application.get(UpdateService.class).updateTo(first).subscribe(b -> {

                                }, t -> log.error("Error", t), () -> {
                                    currentVersion = tagVersion;

                                    sendMessage("New server update to: " + versionName,
                                            "Server has been updated please use: " +
                                                    application.getConfig().getDiscordConfig()
                                                            .getCommandPrefix() +
                                                    "restart to update now, or the bot will restart " +
                                                    "automatically in 10 minutes.." +
                                                    ".");

                                    timer.schedule(new TimerTask()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            sendMessage("Bot is now restarting for the update...");
                                            application.restart();
                                        }
                                    }, 1000 * 60 * 10);

                                });
                            }
                        } else
                        {
                            log.info("Up to date");
                        }
                    }
                    catch (Exception e)
                    {
                        log.error(
                                "Checking for update failed.... Probably a rate limit.. please wait an hour" +
                                        ".", e);
                    }
                });
            }
        }, 3000, 1000 * 60 * 60);
    }

    public synchronized void sendMessage(String title, String message)
    {

        Function<Guild, EmbedCreateSpec> specConsumer = (g) -> {
            Optional<Color> col = facade.getClient().getSelf()
                    .flatMap(u -> u.asMember(g.getId()))
                    .flatMap(PartialMember::getColor).blockOptional();

            return EmbedCreateSpec.builder()
                    .footer(EmbedCreateFields.Footer.of("Server Version: " + application.getServerVersion(),
                            null))
                    .author("BabbleBot", "https://github.com/bendavies99/BabbleBot-Server", null)
                    .timestamp(Instant.now())
                    .color(col.orElse(Color.BLUE))
                    .title(title)
                    .description("```\n" + message + "```")
                    .build();
        };

        announcementChannelRepo.findAll().forEach(ac -> facade.getClient()
                .getGuildById(Snowflake.of(ac.getGuild().getId().toLong()))
                .subscribe(g -> g.getChannelById(Snowflake.of(ac.getChannel().getId().toLong()))
                        .cast(TextChannel.class)
                        .subscribe(ch -> ch.createMessage(specConsumer.apply(g)))));
    }

    public synchronized void sendMessage(String message)
    {
        sendMessage("New announcement", message);
    }

    @Data
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TagItem
    {
        private String tag_name;
        private List<Asset> assets;
        private boolean prerelease;
    }

    @Data
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Asset
    {
        private String browser_download_url;
    }
}
