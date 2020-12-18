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

package net.bdavies.discord.services;

import com.google.inject.Inject;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import lombok.extern.log4j.Log4j2;
import net.bdavies.command.CommandDispatcher;
import net.bdavies.command.parser.DiscordMessageParser;
import reactor.core.publisher.Mono;
import net.bdavies.api.IApplication;
import net.bdavies.api.command.ICommandDispatcher;
import net.bdavies.api.config.IDiscordConfig;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public class Discord4JBotMessageService {

    private final GatewayDiscordClient client;

    private final IDiscordConfig config;

    private final IApplication application;

    private final ICommandDispatcher commandDispatcher;

    @Inject
    public Discord4JBotMessageService(GatewayDiscordClient client, IDiscordConfig config, IApplication application,
      ICommandDispatcher commandDispatcher) {
        this.client = client;
        this.config = config;
        this.application = application;
        this.commandDispatcher = commandDispatcher;
    }

    public void register() {
        AtomicBoolean hasSentMessage = new AtomicBoolean(false);
        client.getEventDispatcher().on(MessageCreateEvent.class)
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
                cd.execute(new DiscordMessageParser(e.getMessage()), m.replace(config.getCommandPrefix(), ""),
                  application);
            }));
    }
}
