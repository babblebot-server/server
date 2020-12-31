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

import com.google.inject.Inject;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.config.IDiscordConfig;
import net.bdavies.discord.services.Discord4JBotMessageService;

/**
 * This class will setup Discord4J by making a {@link discord4j.core.DiscordClient}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class Discord4JSetup
{

    private final IApplication application;
    private final IDiscordConfig config;
    @Getter
    private GatewayDiscordClient client;

    @Inject
    public Discord4JSetup(IApplication application, IDiscordConfig config)
    {
        this.application = application;
        this.config = config;
        this.setupClient();
    }

    private void setupClient()
    {
        try
        {
            log.info("Setting up Discord Client");

            client = DiscordClient.builder(config.getToken()).build().login().block();
            assert client != null;
            client.getEventDispatcher().on(ReadyEvent.class).subscribe(e -> {
                client.updatePresence(Presence.online(Activity.playing(config.getPlayingText())))
                       .block();
                log.info("Started Discord Client, waiting for messages...");
            });
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    public void startServices()
    {
        Discord4JBotMessageService messageService = application.get(Discord4JBotMessageService.class);
        messageService.register();

    }
}
