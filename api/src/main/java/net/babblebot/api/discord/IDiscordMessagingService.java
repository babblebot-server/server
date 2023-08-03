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

package net.babblebot.api.discord;

import net.babblebot.api.obj.message.discord.DiscordChannel;
import net.babblebot.api.obj.message.discord.DiscordGuild;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.api.obj.message.discord.DiscordUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for sending messages through discord to a channel or a private message etc
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.30
 */
@Service
public interface IDiscordMessagingService
{
    Optional<DiscordMessage> send(DiscordGuild guild, DiscordChannel channel,
                                  DiscordMessageSendSpec spec);
    Optional<DiscordMessage> send(long guild, long channel, DiscordMessageSendSpec spec);

    Optional<DiscordMessage> sendPrivateMessage(DiscordGuild guild, DiscordUser user,
                                                DiscordMessageSendSpec spec);

    Optional<DiscordMessage> sendPrivateMessage(long guild, long user,
                                                DiscordMessageSendSpec spec);

    void deleteMessage(DiscordMessage message);
}
