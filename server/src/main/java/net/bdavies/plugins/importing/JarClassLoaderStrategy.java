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

package net.bdavies.plugins.importing;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.config.IPluginConfig;
import net.bdavies.api.plugins.IPlugin;
import net.bdavies.api.plugins.Plugin;
import org.springframework.stereotype.Service;
import org.xeustechnologies.jcl.JarClassLoader;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
@Service
public class JarClassLoaderStrategy implements IPluginImportStrategy
{

    private final JarClassLoader jcl;

    private final IApplication application;

    public JarClassLoaderStrategy(IApplication application)
    {
        this.application = application;
        this.jcl = new JarClassLoader();
    }

    @Override
    public Flux<Object> importPlugin(IPluginConfig config)
    {
        String pluginLocation = "plugins/" + config.getPluginLocation().replace("plugins/", "");
        String finalPluginLocation;

        if (pluginLocation.contains(".jar"))
        {
            finalPluginLocation = config.getPluginLocation();
            jcl.add(config.getPluginLocation());
        } else
        {
            finalPluginLocation = pluginLocation + "/";
            jcl.add(pluginLocation + "/");
        }

        try
        {
            if (config.getPluginClassPath().equals(""))
            {
                //Load All
                return Flux.create(sink -> {
                    log.info("Attempting to load plugin in dir: {}", finalPluginLocation);
                    List<String> check = Arrays.asList("Proxy", "java/lang", "com/sun", "Guice", "google",
                            "GeneratedMethodAccessor", "GeneratedConstructorAccessor", "javax");
                    if (jcl.getLoadedResources().keySet().isEmpty())
                    {
                        log.error("Unable to load resources in dir: {}", finalPluginLocation);
                    }
                    jcl.getLoadedResources().keySet().stream()
                            .map(c -> {
                                if (c.endsWith(".class") && !c.contains("module-info"))
                                {
                                    try
                                    {
                                        return jcl.loadClass(c.replaceAll("/", ".").replace(".class", ""));
                                    }
                                    catch (ClassNotFoundException | NoClassDefFoundError e)
                                    {
                                        if (contains(c, e.getMessage(), check))
                                        {
                                            return null;
                                        } else
                                        {
                                            log.error("Failed to load class: " + c +
                                                    ", please ensure that is is included in the libs.", e);
                                        }
                                        return null;
                                    }
                                }
                                return null;
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


            } else
            {
                Class<?> clazz = jcl.loadClass(config.getPluginClassPath());
                return Flux.just(application.get(clazz));
            }
        }
        catch (ClassNotFoundException e)
        {
            log.info("Plugin cannot be found please check your classpath.");
        }

        return Flux.empty();
    }

    private boolean contains(String s, String s1, List<String> contains)
    {
        AtomicBoolean b = new AtomicBoolean(false);

        contains.forEach(c -> {
            if (!b.get())
            {
                if (s.contains(c) || s1.contains(c))
                {
                    b.set(true);
                }
            }
        });

        return b.get();
    }
}
