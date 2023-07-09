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

package net.babblebot.discord.services;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.IApplication;
import net.babblebot.api.discord.DiscordMessageSendSpec;
import net.babblebot.api.discord.IDiscordMessagingService;
import net.babblebot.api.obj.message.discord.*;
import net.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.babblebot.discord.DiscordFacade;
import net.babblebot.discord.obj.factories.DiscordObjectFactory;
import net.babblebot.discord.obj.factories.EmbedMessageFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for sending messages through discord to a channel or a private message
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordMessagingService implements IDiscordMessagingService
{
    private final DiscordFacade facade;
    private final IApplication application;
    private final DiscordObjectFactory discordObjectFactory;

    @Override
    public Mono<DiscordMessage> send(DiscordGuild guild, DiscordChannel channel,
                                     DiscordMessageSendSpec spec)
    {
        return send(guild.getId().toLong(), channel.getId().toLong(), spec);
    }

    @Override
    public Mono<DiscordMessage> send(long guild, long channel, DiscordMessageSendSpec spec)
    {
        Guild g = facade.getClient().getGuildById(Snowflake.of(guild)).blockOptional().orElseThrow();
        TextChannel c = g.getChannelById(Snowflake.of(channel)).cast(TextChannel.class).blockOptional()
                .orElseThrow();
        Mono<DiscordMessage> msgMono = c.createMessage(getMessageCreateSpec(spec, g))
                .map(m -> messageToDiscordMessage(m, g.getId().asLong(), false));
        return c.typeUntil(Mono.just("")).elementAt(0).flatMap(m -> msgMono);
    }

    private MessageCreateSpec getMessageCreateSpec(DiscordMessageSendSpec spec, Guild g)
    {
        return MessageCreateSpec.builder()
                .tts(spec.isTts())
                .addAllEmbeds(getEmbedsFromSpec(spec.getEmbeds(), g))
                .content(spec.getContent())
                .build();
    }

    private DiscordMessage messageToDiscordMessage(Message m, long guildId, boolean isPrivate)
    {
        val g = discordObjectFactory.guildFromId(DiscordId.from(guildId)).orElse(null);
        DiscordChannel ch = null;
        if (g != null)
        {
            ch = discordObjectFactory.channelFromId(g.getId(), DiscordId.from(m.getChannelId().asLong()))
                    .orElse(null);
        }
        val user = discordObjectFactory.userFromId(
                        DiscordId.from(m.getAuthor().orElseThrow().getId().asLong()))
                .orElseThrow();


        return DiscordMessage.builder()
                .guild(g)
                .channel(ch)
                .author(user)
                .isPrivateMessage(isPrivate)
                .content(m.getContent())
                .id(DiscordId.from(m.getId().asLong()))
                .build();
    }

    private List<EmbedCreateSpec> getEmbedsFromSpec(List<EmbedMessage> embeds, Guild g)
    {
        if (embeds == null)
        {
            return new ArrayList<>();
        }
        return embeds.stream().map(em -> {
            EmbedMessageFactory.addDefaults(em, application, Mono.just(g));
            return EmbedMessageFactory.fromBabblebot(em);
        }).collect(Collectors.toList());
    }

    @Override
    public Mono<DiscordMessage> sendPrivateMessage(DiscordGuild guild, DiscordUser user,
                                                   DiscordMessageSendSpec spec)
    {
        return sendPrivateMessage(guild.getId().toLong(), user.getId().toLong(), spec);
    }

    @Override
    public Mono<DiscordMessage> sendPrivateMessage(long guild, long user, DiscordMessageSendSpec spec)
    {
        Guild g = facade.getClient().getGuildById(Snowflake.of(guild)).blockOptional().orElseThrow();
        Member m = g.getMemberById(Snowflake.of(user)).blockOptional().orElseThrow();
        return m.getPrivateChannel().blockOptional()
                .orElseThrow()
                .createMessage(getMessageCreateSpec(spec, g))
                .map(msg -> messageToDiscordMessage(msg, g.getId().asLong(), true));
    }

    @Override
    public Mono<Void> deleteMessage(DiscordMessage message)
    {
        Message m = discordObjectFactory.messageFromBabblebot(message).blockOptional()
                .orElseThrow();
        return m.delete();
    }

    @Override
    public Optional<DiscordMessage> sendSync(DiscordGuild guild, DiscordChannel channel,
                                             DiscordMessageSendSpec spec)
    {
        return send(guild, channel, spec).blockOptional();
    }

    @Override
    public Optional<DiscordMessage> sendSync(long guild, long channel, DiscordMessageSendSpec spec)
    {
        return send(guild, channel, spec).blockOptional();
    }

    @Override
    public Optional<DiscordMessage> sendPrivateMessageSync(DiscordGuild guild, DiscordUser user,
                                                           DiscordMessageSendSpec spec)
    {
        return sendPrivateMessage(guild, user, spec).blockOptional();
    }

    @Override
    public Optional<DiscordMessage> sendPrivateMessageSync(long guild, long user, DiscordMessageSendSpec spec)
    {
        return sendPrivateMessage(guild, user, spec).blockOptional();
    }

    @Override
    public void deleteMessageSync(DiscordMessage message)
    {
        deleteMessage(message).block();
    }
}
