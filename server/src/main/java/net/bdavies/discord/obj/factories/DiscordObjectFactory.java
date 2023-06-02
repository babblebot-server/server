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

package net.bdavies.discord.obj.factories;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.discord.IDiscordFacade;
import net.bdavies.api.obj.message.discord.DiscordChannel;
import net.bdavies.api.obj.message.discord.DiscordGuild;
import net.bdavies.api.obj.message.discord.DiscordId;
import net.bdavies.api.obj.message.discord.DiscordUser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Factory for creating Discord object from Babblebot to Internal API
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.5
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordObjectFactory
{
    private final IDiscordFacade discordFacade;

    private Mono<Guild> guildFromBabbleBot(DiscordGuild guild)
    {
        return discordFacade.getClient().getGuildById(Snowflake.of(guild.getId().toLong()));
    }

    public Optional<DiscordGuild> guildFromId(DiscordId id)
    {
        return guildFromBabbleBot(DiscordGuild.builder().id(id).build())
                .map(g -> DiscordGuild.builder()
                        .id(id)
                        .name(g.getName())
                        .build())
                .cast(DiscordGuild.class)
                .blockOptional();
    }

    private Mono<TextChannel> channelFromBabbleBot(DiscordGuild guild, DiscordChannel channel)
    {
        return discordFacade.getClient()
                .getGuildById(Snowflake.of(guild.getId().toLong()))
                .flatMap(g -> g.getChannelById(Snowflake.of(channel.getId().toLong())))
                .cast(TextChannel.class);
    }

    public Optional<DiscordChannel> channelFromId(DiscordId guildId, DiscordId channelId)
    {
        return channelFromBabbleBot(
                DiscordGuild.builder().id(guildId).build(),
                DiscordChannel.builder().id(channelId).build()
        ).map(ch -> DiscordChannel.builder()
                        .id(channelId)
                        .guildId(guildId)
                        .name(ch.getName())
                        .build())
                .cast(DiscordChannel.class)
                .blockOptional();
    }

    private Mono<User> userFromBabblebot(DiscordUser user)
    {
        return discordFacade.getClient()
                .getUserById(Snowflake.of(user.getId().toLong()));
    }

    public Optional<DiscordUser> userFromId(DiscordId userId)
    {
        return userFromBabblebot(DiscordUser.builder().id(userId).build())
                .map(u -> DiscordUser.builder()
                        .id(userId)
                        .name(u.getUsername())
                        .build())
                .cast(DiscordUser.class)
                .blockOptional();
    }
}
