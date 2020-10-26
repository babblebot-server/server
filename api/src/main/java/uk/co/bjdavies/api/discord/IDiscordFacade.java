package uk.co.bjdavies.api.discord;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

/**
 * This is the Public API for the Discord4JWrapper of the Discord API this will be used for plugins
 * <p>
 * An example use case being calling {@link IDiscordFacade#getClient()}  in a plugin will give you access to the
 * {@link DiscordClient}
 * Use DiscordClient at your own risk it is subject to change, I would recommend just using the api given to you
 * through the facade.
 * </p>
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IDiscordFacade {
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
     * To use try doing {@code facade.getOurUser().subscribe(user -> System.out.println(user.getUsername()));} look
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
