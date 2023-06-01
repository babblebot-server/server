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

package net.bdavies.api.discord;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

/**
 * This is the Public API for the Discord4JWrapper of the Discord API this will be used for plugins
 * <p>
 * An example use case being calling {@link IDiscordFacade#getClient()}  in a plugin will give you access
 * to the
 * {@link DiscordClient}
 * Use DiscordClient at your own risk it is subject to change, I would recommend just using the api given
 * to you
 * through the facade.
 * </p>
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Component
public interface IDiscordFacade
{
    /**
     * This is available to the public through plugins and this will allow for a bot to be logged out
     * I wouldn't recommend using this only if you would like to implement a logout command for the bot.
     *
     * @return {@link Mono} this is a Mono Stream of a Mono
     */
    Mono<Void> logoutBot();

    /**
     * This will return the discord client.
     *
     * @return {@link GatewayDiscordClient}
     */
    GatewayDiscordClient getClient();

    /**
     * This is available to the public through plugins and this will return the bot user.
     * To use try doing {@code facade.getOurUser().subscribe(user -> System.out.println(user.getUsername())
     * );} look
     * at {@link Mono#subscribe(java.util.function.Consumer)}
     *
     * @return {@link Mono} this is a Mono Stream of a User
     */
    Mono<User> getOurUser();

    /**
     * This will update the presence of the bot to the text
     *
     * @param text {@link String} the text to change it to
     * @see discord4j.core.GatewayDiscordClient#updatePresence(discord4j.discordjson.json.gateway.StatusUpdate)
     * @see Presence
     * @see Activity
     */
    void updateBotPlayingText(String text);

    /**
     * Register a Event Listener to the Discord Client.
     *
     * @param callback A callback for the event handler
     * @param clazz    The the Event Class
     */
    <T extends Event> void registerEventHandler(Class<T> clazz, Consumer<T> callback);
}
