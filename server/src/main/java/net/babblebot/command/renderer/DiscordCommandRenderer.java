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

package net.babblebot.command.renderer;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.command.IResponse;
import net.babblebot.api.discord.DiscordMessageSendSpec;
import net.babblebot.api.discord.IDiscordMessagingService;
import net.babblebot.command.ResponseFactory;
import net.babblebot.command.errors.UsageException;

/**
 * Discord Command Renderer that will send the response to the channel
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.23
 */
@Slf4j
@RequiredArgsConstructor
public class DiscordCommandRenderer implements CommandRenderer
{
    private final TextChannel channel;
    private final Guild guild;
    private final IDiscordMessagingService service;

    @Override
    public void render(IResponse response)
    {
        long guildId = guild.getId().asLong();
        long channelId = channel.getId().asLong();
        if (response.isStringResponse())
        {
            service.send(guildId, channelId,
                            DiscordMessageSendSpec.fromString(response.getStringResponse()))
                    .subscribe();
        } else if (response.isTTSResponse())
        {
            service.send(guildId, channelId,
                            DiscordMessageSendSpec.fromTts(response.getTTSMessage().getContent()))
                    .subscribe();
        } else
        {
            service.send(guildId, channelId,
                            DiscordMessageSendSpec.fromEmbed(response.getEmbedCreateSpecResponse().get()))
                    .subscribe();
        }
    }

    @Override
    public boolean onError(Exception e)
    {
        if (e instanceof UsageException)
        {
            render(ResponseFactory.createStringResponse(e.getLocalizedMessage()));
            return true;
        }

        return false;
    }
}
