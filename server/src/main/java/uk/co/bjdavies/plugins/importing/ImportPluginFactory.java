package uk.co.bjdavies.plugins.importing;

import reactor.core.publisher.Flux;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.config.IPluginConfig;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public class ImportPluginFactory {

    public static Flux<Object> importPlugin(IPluginConfig config, IApplication application) {
        if (config.getPluginType().toLowerCase().equals("java")) {
            return application.get(JarClassLoaderStrategy.class).importPlugin(config);
        }
        return Flux.empty();
    }

}
