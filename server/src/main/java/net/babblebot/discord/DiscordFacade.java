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

package net.babblebot.discord;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.IApplication;
import net.babblebot.api.IDiscordFacade;
import net.babblebot.api.obj.message.discord.DiscordUser;
import net.babblebot.discord.obj.factories.DiscordUserFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.stereotype.Component;

/**
 * This is the Public API for the Discord4JWrapper of the Discord API this will be used for plugins
 * It will include common utilities that will be required to create plugins
 * <p>
 * An example use case being calling {@link #getClient()}  in a plugin will give you access to the
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
    private final JDA client;

    @Getter
    private final IApplication application;

    public DiscordFacade(IApplication application, DiscordLoginService setup)
    {
        this.application = application;
        this.client = setup.getClient();
    }

    /**
     * This is available to the public through plugins and this will allow for a bot to be logged out
     * I wouldn't recommend using this only if you would like to implement a logout command for the bot.
     */
    public void logoutBot()
    {
        log.info("Logging DiscordBot out!");
        this.client.shutdownNow();
    }

    /**
     * This is available to the public through plugins and this will return the bot user.
     * <p>
     * To use try doing {@code facade.getOurUser().subscribe(user -> System.out.println(user.getUsername()));}
     *
     * @return the bot user
     */
    public DiscordUser getBotUser()
    {
        DiscordUserFactory userFactory = application.get(DiscordUserFactory.class);
        return userFactory.makeFromInternal(client.getSelfUser());
    }

    /**
     * This will update the presence of the bot to the text
     *
     * @param text {@link String} the text to change it to
     */
    public void updateBotPlayingText(String text)
    {
        this.client.getPresence().setActivity(Activity.playing(text));
    }
}
