package uk.co.bjdavies.plugins;

import com.google.inject.AbstractModule;
import lombok.Getter;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.plugins.IPluginContainer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class PluginModule extends AbstractModule {

    @Getter
    private final IPluginContainer pluginContainer;

    public PluginModule(IApplication application) {
        pluginContainer = new PluginContainer(application);
    }

    protected void configure() {
        bind(IPluginContainer.class).toInstance(this.pluginContainer);
    }
}
