package uk.co.bjdavies.plugins.importing;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.xeustechnologies.jcl.JarClassLoader;
import reactor.core.publisher.Flux;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.config.IPluginConfig;
import uk.co.bjdavies.api.plugins.IPlugin;
import uk.co.bjdavies.api.plugins.Plugin;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
public class JarClassLoaderStrategy implements IPluginImportStrategy {

    private final JarClassLoader jcl;

    private final IApplication application;

    @Inject
    public JarClassLoaderStrategy(IApplication application) {
        this.application = application;
        this.jcl = new JarClassLoader();
    }

    @Override
    public Flux<Object> importPlugin(IPluginConfig config) {
        String pluginLocation = "plugins/" + config.getPluginLocation().replace("plugins/", "");

        if (pluginLocation.contains(".jar")) {
            jcl.add(config.getPluginLocation());
        } else {
            jcl.add(pluginLocation + "/");
        }

        try {


            if (config.getPluginClassPath().equals("")) {
                //Load All
                return Flux.create(sink -> {
                    log.info("Loading plugins: ");
                    jcl.getLoadedResources().keySet().stream()
                            .map(c -> {
                                try {
                                    return jcl.loadClass(c.replaceAll("/", ".").replace(".class", ""));
                                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .filter(c -> c.isAnnotationPresent(Plugin.class) ||
                                    Arrays.stream(c.getInterfaces()).anyMatch(i -> i == IPlugin.class))
                            .forEach(c -> {
                                //noinspection unchecked
                                sink.next(application.get(c));
                            });
                    sink.complete();
                });


            } else {
                Class<?> clazz = jcl.loadClass(config.getPluginClassPath());
                return Flux.just(application.get(clazz));
            }
        } catch (ClassNotFoundException e) {
            log.info("Plugin cannot be found please check your classpath.");
        }

        return Flux.empty();
    }
}
