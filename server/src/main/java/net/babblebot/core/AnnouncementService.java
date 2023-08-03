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

package net.babblebot.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.IApplication;
import net.babblebot.api.core.IAnnouncementService;
import net.babblebot.api.discord.DiscordMessageSendSpec;
import net.babblebot.api.discord.IDiscordMessagingService;
import net.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.babblebot.core.repository.AnnouncementChannelRepository;
import net.babblebot.discord.obj.factories.DiscordChannelFactory;
import org.springframework.stereotype.Service;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementService implements IAnnouncementService
{
    private final IApplication application;
    private final AnnouncementChannelRepository announcementChannelRepo;
    private final DiscordChannelFactory discordChannelFactory;
    private final IDiscordMessagingService messagingService;

    public synchronized void sendMessage(String title, String message)
    {
        announcementChannelRepo.findAll().forEach(ac -> {
            val em = EmbedMessage.builder()
                    .title(title)
                    .description("```\n" + message + "```")
                    .build();
            messagingService.send(ac.getGuild(),
                    ac.getChannel(),
                    DiscordMessageSendSpec
                            .fromEmbed(em));
        });
    }

    public synchronized void sendMessage(String message)
    {
        sendMessage("New announcement", message);
    }
}
