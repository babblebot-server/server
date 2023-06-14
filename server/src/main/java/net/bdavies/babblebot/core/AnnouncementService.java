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

package net.bdavies.babblebot.core;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.command.IResponse;
import net.bdavies.babblebot.api.core.IAnnouncementService;
import net.bdavies.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.bdavies.babblebot.command.ResponseFactory;
import net.bdavies.babblebot.command.renderer.DiscordCommandRenderer;
import net.bdavies.babblebot.core.repository.AnnouncementChannelRepository;
import net.bdavies.babblebot.discord.DiscordFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
@Service
public class AnnouncementService implements IAnnouncementService
{
    private final DiscordFacade facade;

    private final IApplication application;
    private final AnnouncementChannelRepository announcementChannelRepo;

    @Autowired
    public AnnouncementService(DiscordFacade facade, IApplication application,
                               AnnouncementChannelRepository announcementChannelRepo)
    {
        this.facade = facade;
        this.application = application;
        this.announcementChannelRepo = announcementChannelRepo;
    }

    public synchronized void sendMessage(String title, String message)
    {
        announcementChannelRepo.findAll().forEach(ac -> facade.getClient()
                .getGuildById(Snowflake.of(ac.getGuild().getId().toLong()))
                .subscribe(g -> g.getChannelById(Snowflake.of(ac.getChannel().getId().toLong()))
                        .cast(TextChannel.class)
                        .subscribe(ch -> {
                            var renderer = new DiscordCommandRenderer(ch, application);
                            val em = EmbedMessage.builder()
                                    .title(title)
                                    .description("```\n" + message + "```")
                                    .build();
                            IResponse response = ResponseFactory.createEmbedResponse(em);
                            renderer.render(response);
                        })));
    }

    public synchronized void sendMessage(String message)
    {
        sendMessage("New announcement", message);
    }
}
