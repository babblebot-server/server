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

package net.bdavies.babblebot.command.renderer;

import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.command.IResponse;
import net.bdavies.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.bdavies.babblebot.command.errors.UsageException;
import net.bdavies.babblebot.discord.obj.factories.EmbedMessageFactory;

/**
 * Discord Command Renderer that will send the response to the channel
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.18
 */
@Slf4j
@RequiredArgsConstructor
public class DiscordCommandRenderer implements CommandRenderer
{
    private final TextChannel channel;
    private final IApplication application;

    @Override
    public void render(IResponse response)
    {
        if (response.isStringResponse())
        {
            channel.createMessage(response.getStringResponse()).subscribe();
        } else
        {
            EmbedMessage em = response.getEmbedCreateSpecResponse().get();
            EmbedMessageFactory.addDefaults(em, application, channel.getGuild());
            EmbedCreateSpec spec = EmbedMessageFactory.fromBabblebot(em);
            channel.typeUntil(channel.createMessage(spec)).subscribe();
        }
    }

    @Override
    public boolean onError(Exception e)
    {
        if (e instanceof UsageException)
        {
            channel.createMessage(e.getMessage()).subscribe();
            return true;
        }

        return false;
    }
}
