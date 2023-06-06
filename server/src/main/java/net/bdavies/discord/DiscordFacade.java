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

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Presence;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.discord.IDiscordFacade;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

/**
 * This is the Public API for the Discord4JWrapper of the Discord API this will be used for plugins
 * It will include common utilities that will be required to create plugins
 * <p>
 * An example use case being calling {@link #getClient()}  in a plugin will give you access to the
 * {@link DiscordClient}
 * Use DiscordClient at your own risk it is subject to change, I would recommend just using the api given
 * to you
 * through the facade.
 * </p>
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@Component
public class DiscordFacade implements IDiscordFacade
{

    @Getter
    private final GatewayDiscordClient client;

    @Getter
    private final IApplication application;

    public DiscordFacade(IApplication application, Discord4JService setup)
    {
        this.application = application;
        this.client = setup.getClient();
    }

    /**
     * This is available to the public through plugins and this will allow for a bot to be logged out
     * I wouldn't recommend using this only if you would like to implement a logout command for the bot.
     *
     * @return {@link Mono<Void>} this is a Mono Stream of a Mono
     */
    public Mono<Void> logoutBot()
    {
        log.info("Logging DiscordBot out!");
        return this.client.logout();
    }

    /**
     * This is available to the public through plugins and this will return the bot user.
     * <p>
     * To use try doing {@code facade.getOurUser().subscribe(user -> System.out.println(user.getUsername()));}
     * look at {@link Mono#subscribe(java.util.function.Consumer)}
     *
     * @return {@link Mono<User>} this is a Mono Stream of a User
     */
    public Mono<User> getOurUser()
    {
        return this.client.getSelf();
    }

    /**
     * This will update the presence of the bot to the text
     *
     * @param text {@link String} the text to change it to
     * @see GatewayDiscordClient#updatePresence(discord4j.core.object.presence.ClientPresence)
     * @see Presence
     * @see Activity
     */
    public void updateBotPlayingText(String text)
    {
        this.client.updatePresence(ClientPresence.online(ClientActivity.playing(text))).block();
    }

    /**
     * Register a Event Listener to the Discord Client.
     *
     * @param callback A callback for the event handler
     * @param clazz    The Event Class
     */
    public <T extends Event> void registerEventHandler(Class<T> clazz, Consumer<T> callback)
    {
        if (this.client != null)
        {
            this.client.getEventDispatcher().on(clazz).subscribe(callback);
        } else
        {
            log.error("Unable to register event dispatcher because the client token is null or invalid");
        }
    }
}
