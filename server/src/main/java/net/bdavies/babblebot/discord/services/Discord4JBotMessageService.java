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

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
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
import net.bdavies.babblebot.command.renderer.CommandRenderer;
import net.bdavies.babblebot.command.renderer.DiscordCommandRenderer;
import net.bdavies.babblebot.command.renderer.DiscordInteractionEventRenderer;
import net.bdavies.babblebot.connect.ConnectConfigurationProperties;
import net.bdavies.babblebot.connect.DiscordConnectMessage;
import net.bdavies.babblebot.connect.queue.DiscordConnectQueue;
import net.bdavies.babblebot.discord.Discord4JService;
import net.bdavies.babblebot.discord.DiscordMessageType;
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
    private final ConnectConfigurationProperties connectConfig;

    public void register()
    {
        if (connectConfig.isLeader())
        {
            AtomicBoolean hasSentMessage = new AtomicBoolean(false);
            service.getClient().getEventDispatcher().on(MessageCreateEvent.class).subscribe(
                    e -> Mono.justOrEmpty(e.getMessage().getContent())
                            .filter(m -> m.startsWith(config.getCommandPrefix())).filterWhen(
                                    m -> e.getMessage().getAuthorAsMember().map(User::isBot)
                                            .map(isBot -> !isBot)).subscribe(m -> {
                                CommandDispatcher cd = (CommandDispatcher) commandDispatcher;
                                long g = e.getMessage().getGuild().map(gld -> gld.getId().asLong())
                                        .blockOptional().orElseThrow();
                                long ch = e.getMessage().getChannel().map(chl -> chl.getId().asLong())
                                        .blockOptional().orElseThrow();
                                long author = e.getMessage().getAuthor().orElseThrow().getId().asLong();
                                long msgId = e.getMessage().getId().asLong();
                                String msg = m.replace(config.getCommandPrefix(), "");

                                val discordMessage = buildMessage(g, ch, author, msgId, msg, "");
                                val dmp = new DiscordMessageParser(discordMessage);
                                val renderer = new DiscordCommandRenderer(
                                        e.getMessage().getChannel().cast(TextChannel.class).blockOptional()
                                                .orElseThrow(), application);

                                if (!connectConfig.isUseConnect() || connectConfig.isWorker())
                                {
                                    cd.execute(dmp, msg, application, renderer);
                                } else
                                {
                                    DiscordConnectQueue connectQueue = application.get(
                                            DiscordConnectQueue.class);
                                    log.info("Sending message: {} to worker", discordMessage);
                                    connectQueue.send(DiscordConnectMessage.builder()
                                            .type(DiscordMessageType.COMMAND)
                                            .message(discordMessage)
                                            .build());
                                }
                            }));

            service.getClient().getEventDispatcher().on(ChatInputInteractionEvent.class).subscribe(e -> {
                CommandDispatcher cd = (CommandDispatcher) commandDispatcher;
                long g = e.getInteraction().getGuild().map(gld -> gld.getId().asLong()).blockOptional()
                        .orElseThrow();
                long ch = e.getInteraction().getChannel().map(chl -> chl.getId().asLong()).blockOptional()
                        .orElseThrow();
                long author = e.getInteraction().getUser().getId().asLong();
                long msgId = e.getInteraction().getId().asLong();
                StringBuilder msg = new StringBuilder(e.getCommandName());
                e.getOptions().forEach(o -> {
                    msg.append(" -").append(o.getName());
                    if (o.getValue().isPresent())
                    {
                        msg.append("=").append(o.getValue().get().getRaw());
                    }
                });

                val discordMessage = buildMessage(g, ch, author, msgId, msg.toString(),
                        e.getInteraction().getToken());
                val dmp = new DiscordMessageParser(discordMessage);

                if (!connectConfig.isUseConnect() || connectConfig.isWorker())
                {
                    val renderer = new DiscordInteractionEventRenderer(
                            application.get(GatewayDiscordClient.class), discordMessage,
                            e.getInteraction().getChannel().cast(TextChannel.class).blockOptional()
                                    .orElseThrow(), application);
                    cd.execute(dmp, msg.toString(), application, renderer);
                } else
                {
                    DiscordConnectQueue connectQueue = application.get(DiscordConnectQueue.class);
                    log.info("Sending message: {} to worker", discordMessage);
                    connectQueue.send(DiscordConnectMessage.builder()
                            .type(DiscordMessageType.INTERACTION)
                            .message(discordMessage)
                            .build());
                }
            });
        }
    }

    public void registerConnectHandler()
    {
        if (!connectConfig.isLeader())
        {
            DiscordConnectQueue connectQueue = application.get(DiscordConnectQueue.class);
            connectQueue.setMessageHandler(cm -> {
                CommandDispatcher cd = (CommandDispatcher) commandDispatcher;
                DiscordMessageParser messageParser = new DiscordMessageParser(cm.getMessage());
                DiscordMessage message = cm.getMessage();
                TextChannel channel = discordObjectFactory.channelFromBabbleBot(message.getGuild(),
                        message.getChannel()).blockOptional().orElseThrow();
                CommandRenderer renderer = new DiscordCommandRenderer(channel, application);
                if (cm.getType() == DiscordMessageType.INTERACTION)
                {
                    GatewayDiscordClient discordClient = application.get(GatewayDiscordClient.class);
                    renderer = new DiscordInteractionEventRenderer(discordClient, cm.getMessage(), channel,
                            application);
                }
                cd.execute(messageParser, cm.getMessage().getContent(), application, renderer);
            });
        }
    }

    private DiscordMessage buildMessage(long guildId, long channelId, long authorId, long messageId,
                                        String message, String token)
    {
        val g = discordObjectFactory.guildFromId(DiscordId.from(guildId)).orElseThrow();
        val ch = discordObjectFactory.channelFromId(DiscordId.from(guildId), DiscordId.from(channelId))
                .orElseThrow();
        val user = discordObjectFactory.userFromId(DiscordId.from(authorId)).orElseThrow();


        return DiscordMessage.builder().guild(g).channel(ch).author(user)
                .token(token)
                .content(message)
                .id(DiscordId.from(messageId)).build();
    }
}
