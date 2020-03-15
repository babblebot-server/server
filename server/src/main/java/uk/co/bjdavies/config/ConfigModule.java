package uk.co.bjdavies.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import lombok.Getter;
import uk.co.bjdavies.api.config.IConfig;
import uk.co.bjdavies.api.config.IDiscordConfig;
import uk.co.bjdavies.api.config.IHttpConfig;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class ConfigModule extends AbstractModule {

    @Getter
    private final IConfig config;


    public ConfigModule(String path) {
        this.config = ConfigFactory.makeConfig(path);
    }

    @Override
    protected void configure() {
        bind(IConfig.class).toInstance(this.config);
    }

    @Provides
    private IDiscordConfig provideDiscordConfig(IConfig config) {
        return config.getDiscordConfig();
    }

    @Provides
    private IHttpConfig provideHttpConfig(IConfig config) {
        return config.getHttpConfig();
    }
}
