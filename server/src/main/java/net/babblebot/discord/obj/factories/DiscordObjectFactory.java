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

package net.babblebot.discord.obj.factories;

import discord4j.common.util.Snowflake;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.command.CommandParam;
import net.babblebot.api.command.ICommand;
import net.babblebot.api.obj.message.discord.*;
import net.babblebot.discord.DiscordFacade;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Factory for creating Discord object from Babblebot to Internal API
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.26
 */
@Slf4j
@Component
public class DiscordObjectFactory
{
    private final DiscordFacade discordFacade;

    public DiscordObjectFactory(@Lazy DiscordFacade discordFacade)
    {
        this.discordFacade = discordFacade;
    }

    public Mono<Guild> guildFromBabbleBot(DiscordGuild guild)
    {
        return discordFacade.getClient().getGuildById(Snowflake.of(guild.getId().toLong()));
    }

    public Mono<Message> messageFromBabblebot(DiscordMessage message)
    {
        val messageSnowflake = Snowflake.of(message.getId().toLong());
        if (message.isPrivateMessage())
        {
            val guildSnowflake = Snowflake.of(message.getGuild().getId().toLong());
            val userSnowflake = Snowflake.of(message.getAuthor().getId().toLong());
            return discordFacade.getClient()
                    .getMemberById(guildSnowflake, userSnowflake)
                    .flatMap(User::getPrivateChannel)
                    .flatMap(pc -> pc.getMessageById(messageSnowflake));
        }

        val channelSnowflake = Snowflake.of(message.getChannel().getId().toLong());
        return guildFromBabbleBot(message.getGuild())
                .flatMap(g -> g.getChannelById(channelSnowflake))
                .cast(TextChannel.class)
                .flatMap(ch -> ch.getMessageById(messageSnowflake));
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

    public Mono<TextChannel> channelFromBabbleBot(DiscordGuild guild, DiscordChannel channel)
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

    public Mono<User> userFromBabblebot(DiscordUser user)
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

    public ApplicationCommandRequest createSlashCommand(String namespace, ICommand command)
    {
        String descr = command.getDescription()
                .equals("") ? command.getAliases()[0] : command.getDescription();

        return ApplicationCommandRequest.builder()
                .name(namespace + command.getAliases()[0])
                .description(descr)
                .addAllOptions(
                        Arrays.stream(command.getCommandParams()).map(this::commandParamToOption).collect(
                                Collectors.toList()))
                .build();
    }

    private ApplicationCommandOptionData commandParamToOption(CommandParam commandParam)
    {
        return ApplicationCommandOptionData.builder()
                .name(commandParam.value())
                .required(!commandParam.optional())
                .description(commandParam.value())
                .type(commandParam.canBeEmpty() ? ApplicationCommandOption.Type.BOOLEAN.getValue()
                        : ApplicationCommandOption.Type.STRING.getValue())
                .build();
    }
}
