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

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.core.IAnnouncementService;
import net.bdavies.api.discord.IDiscordFacade;
import net.bdavies.core.repository.AnnouncementChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
@Service
public class AnnouncementService implements IAnnouncementService
{
    private final IDiscordFacade facade;

    private final IApplication application;
    private final AnnouncementChannelRepository announcementChannelRepo;

    @Autowired
    public AnnouncementService(IDiscordFacade facade, IApplication application,
                               AnnouncementChannelRepository announcementChannelRepo)
    {
        this.facade = facade;
        this.application = application;
        this.announcementChannelRepo = announcementChannelRepo;
    }

    public synchronized void sendMessage(String title, String message)
    {
        //TODO: pass to the renderer
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
}
