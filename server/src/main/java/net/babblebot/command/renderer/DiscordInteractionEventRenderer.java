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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.command.IResponse;
import net.babblebot.api.discord.DiscordMessageSendSpec;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.command.ResponseFactory;
import net.babblebot.command.errors.UsageException;
import net.babblebot.discord.services.DiscordMessagingService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Optional;

/**
 * Renderer for an Interaction event from Discord
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.29
 */
@Slf4j
@RequiredArgsConstructor
public class DiscordInteractionEventRenderer implements CommandRenderer
{
    private final DiscordMessage message;
    private final DiscordMessagingService service;
    private final SlashCommandInteractionEvent event;
    private boolean replied;

    @Override
    public void render(IResponse response)
    {
        reply(response.getSendSpec());
    }

    private Optional<DiscordMessage> reply(DiscordMessageSendSpec spec)
    {
        if (replied)
        {
            return service.send(message.getGuild(),
                    message.getChannel(), spec);
        }
        Optional<DiscordMessage> resp = service.sendInteractionFollowup(event.getHook(), spec);
        if (resp.isPresent())
        {
            replied = true;
        }
        return resp;
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
