package uk.co.bjdavies.discord;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import discord4j.core.DiscordClient;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.discord.IDiscordFacade;

/**
 * This is a module class that allows to inject discord stuff into a class when using {@link com.google.inject.Inject}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class DiscordModule extends AbstractModule {
    /**
     * Instance of the {@link DiscordClient}
     */
    private final DiscordClient client;


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
    private DiscordClient provideDiscordClient(IDiscordFacade facade) {
        return facade.getClient();
    }
}
