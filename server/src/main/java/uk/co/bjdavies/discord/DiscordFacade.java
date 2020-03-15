package uk.co.bjdavies.discord;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.discord.IDiscordFacade;

import java.util.function.Consumer;

/**
 * This is the Public API for the Discord4JWrapper of the Discord API this will be used for plugins
 * It will include common utilities that will be required to create plugins
 * <p>
 * An example use case being calling {@link #getClient()}  in a plugin will give you access to the {@link DiscordClient}
 * Use DiscordClient at your own risk it is subject to change, I would recommend just using the api given to you through the facade.
 * </p>
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public class DiscordFacade implements IDiscordFacade {

    @Getter
    private final DiscordClient client;

    @Getter
    private final IApplication application;

    public DiscordFacade(DiscordClient client, IApplication application) {
        this.client = client;
        this.application = application;
    }


    /**
     * This is available to the public through plugins and this will allow for a bot to be logged out
     * I wouldn't recommend using this only if you would like to implement a logout command for the bot.
     *
     * @return {@link Mono<Void>} this is a Mono Stream of a Mono
     */
    public Mono<Void> logoutBot() {
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
    public Mono<User> getOurUser() {
        return this.client.getSelf();
    }

    /**
     * This will update the presence of the bot to the text
     *
     * @param text {@link String} the text to change it to
     * @see DiscordClient#updatePresence(Presence)
     * @see Presence
     * @see Activity
     */
    public void updateBotPlayingText(String text) {
        this.client.updatePresence(Presence.online(Activity.playing(text))).block();
    }

    /**
     * Register a Event Listener to the Discord Client.
     *
     * @param callback A callback for the event handler
     * @param clazz    The the Event Class
     */
    public <T extends Event> void registerEventHandler(Class<T> clazz, Consumer<T> callback) {
        this.client.getEventDispatcher().on(clazz).subscribe(callback);
    }
}
