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

package net.bdavies.discord;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.DiscordCommandContext;
import net.bdavies.api.discord.IDiscordCommandUtil;
import net.bdavies.api.obj.message.discord.DiscordMessage;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DiscordCommandUtil implements IDiscordCommandUtil
{

    private final DiscordCommandContext commandContext;
    private final DiscordFacade discordFacade;

    @Override
    public void sendPrivateMessage(String text)
    {
        DiscordMessage message = commandContext.getMessage();
        discordFacade.getClient()
                .getGuildById(Snowflake.of(message.getGuild().getId().toLong()))
                .flatMap(g -> g.getMemberById(Snowflake.of(message.getAuthor().getId().toLong())))
                .flatMap(Member::getPrivateChannel)
                .flatMap(c -> c.createMessage(text))
                .subscribe();
    }
}
