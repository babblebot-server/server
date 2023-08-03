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

package net.babblebot.core.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.command.ICommandContext;
import net.babblebot.api.config.IConfig;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.core.AnnouncementChannel;
import net.babblebot.core.repository.AnnouncementChannelRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Listen Command for the Core Plugin
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.28
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RegisterAnnouncementChannelCommand
{
    private final AnnouncementChannelRepository announcementChannelRepository;
    private final IConfig config;

    public String exec(DiscordMessage message, ICommandContext ctx)
    {
        val g = message.getGuild();
        Optional<AnnouncementChannel> model = announcementChannelRepository.findByGuild(g);
        if (model.isPresent())
        {
            AnnouncementChannel channel = model.get();
            val c = message.getChannel();
            if (c.equals(channel.getChannel()))
            {
                return "Already registered within this server and channel";
            } else
            {
                return "Already registered within this server, on channel: " +
                        channel.getChannel().getName() + ". You can remove it by doing " +
                        config.getDiscordConfig().getCommandPrefix() + "remove-ac on that channel";
            }
        } else
        {
            var channel = new AnnouncementChannel();
            channel.setGuild(g);
            val c = message.getChannel();
            channel.setChannel(c);
            announcementChannelRepository.save(channel);
            return "Registered " + c.getName() + ", as a announcement channel.";
        }
    }
}
