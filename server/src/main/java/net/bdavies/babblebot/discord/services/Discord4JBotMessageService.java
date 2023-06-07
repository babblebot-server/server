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

package net.bdavies.babblebot.discord.services;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.command.ICommandDispatcher;
import net.bdavies.babblebot.api.config.IDiscordConfig;
import net.bdavies.babblebot.api.obj.message.discord.DiscordId;
import net.bdavies.babblebot.api.obj.message.discord.DiscordMessage;
import net.bdavies.babblebot.command.CommandDispatcher;
import net.bdavies.babblebot.command.parser.DiscordMessageParser;
import net.bdavies.babblebot.discord.Discord4JService;
import net.bdavies.babblebot.discord.obj.factories.DiscordObjectFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Discord4JBotMessageService
{

    private final Discord4JService service;

    private final IDiscordConfig config;

    private final IApplication application;

    private final ICommandDispatcher commandDispatcher;
    private final DiscordObjectFactory discordObjectFactory;

    public void register()
    {
        AtomicBoolean hasSentMessage = new AtomicBoolean(false);
        service.getClient().getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(e ->
                        Mono.justOrEmpty(e.getMessage().getContent())
                                .filter(m -> m.startsWith(config.getCommandPrefix()))
                                .filterWhen(m ->
                                        e.getMessage()
                                                .getAuthorAsMember()
                                                .map(User::isBot)
                                                .map(isBot -> !isBot)
                                ).subscribe(m -> {
                                    CommandDispatcher cd = (CommandDispatcher) commandDispatcher;
                                    val g = e.getMessage().getGuild().map(gld -> gld.getId().asLong())
                                            .blockOptional().orElseThrow();
                                    val ch = e.getMessage().getChannel().map(chl -> chl.getId().asLong())
                                            .blockOptional().orElseThrow();
                                    String msg = m.replace(config.getCommandPrefix(), "");
                                    cd.execute(new DiscordMessageParser(
                                                    buildMessage(g, ch,
                                                            e.getMessage().getAuthor().orElseThrow().getId().asLong(),
                                                            e.getMessage().getId().asLong(),
                                                            msg)),
                                            msg,
                                            e.getMessage().getChannel().cast(TextChannel.class),
                                            e.getMessage().getGuild(),
                                            application);
                                }));

        service.getClient().getEventDispatcher().on(ChatInputInteractionEvent.class)
                .subscribe(e -> {
                    CommandDispatcher cd = (CommandDispatcher) commandDispatcher;
                    val g = e.getInteraction().getGuild().map(gld -> gld.getId().asLong())
                            .blockOptional().orElseThrow();
                    val ch = e.getInteraction().getChannel().map(chl -> chl.getId().asLong())
                            .blockOptional().orElseThrow();
                    String msg = e.getCommandName().replace(config.getCommandPrefix(), "");
                    cd.execute(new DiscordMessageParser(
                                    buildMessage(g, ch,
                                            e.getInteraction().getUser().getId().asLong(),
                                            e.getInteraction().getId().asLong(),
                                            msg)),
                            msg,
                            e.getInteraction().getChannel().cast(TextChannel.class),
                            e.getInteraction().getGuild(),
                            application);
                    e.reply(InteractionApplicationCommandCallbackSpec.builder()
                            .content("Resp: ")
                            .build()).subscribe();
                });
    }

    private DiscordMessage buildMessage(long guildId, long channelId, long authorId, long messageId,
                                        String message)
    {
        val g = discordObjectFactory.guildFromId(DiscordId.from(guildId)).orElseThrow();
        val ch = discordObjectFactory.channelFromId(DiscordId.from(guildId), DiscordId.from(channelId))
                .orElseThrow();
        val user = discordObjectFactory.userFromId(DiscordId.from(authorId)).orElseThrow();


        return DiscordMessage.builder()
                .guild(g)
                .channel(ch)
                .author(user)
                .content(message)
                .id(DiscordId.from(messageId))
                .build();
    }
}
