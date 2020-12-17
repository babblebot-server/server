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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.discord.IDiscordFacade;

/**
 * This is a module class that allows to inject discord stuff into a class when using {@link com.google.inject.Inject}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class DiscordModule extends AbstractModule {
    /**
     * Instance of the {@link DiscordClient}
     */
    private final GatewayDiscordClient client;


    private final Discord4JSetup setup;


    private final IApplication application;

    /**
     * Construct a {@link DiscordModule}
     */
    public DiscordModule(IApplication application) {
        this.application = application;
        this.setup = new Discord4JSetup(application, application.getConfig().getDiscordConfig());
        this.client = setup.getClient();
    }

    public void startDiscordBot() {
        this.setup.startServices();
    }

    /**
     * This configures the module to bind classes to instances
     *
     * @see AbstractModule#bind(Class)
     * @see com.google.inject.binder.AnnotatedBindingBuilder#toInstance(Object)
     */
    @Override
    protected void configure() {
        bind(IDiscordFacade.class).toInstance(new DiscordFacade(client, application));
    }

    @Provides
    private GatewayDiscordClient provideDiscordClient(IDiscordFacade facade) {
        return facade.getClient();
    }
}
