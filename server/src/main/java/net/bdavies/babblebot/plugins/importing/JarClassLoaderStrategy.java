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

package net.bdavies.babblebot.plugins.importing;

import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.babblebot.BabblebotApplication;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.plugins.IPlugin;
import net.bdavies.babblebot.api.plugins.Plugin;
import net.bdavies.babblebot.api.plugins.PluginConfig;
import net.bdavies.babblebot.plugins.PluginModel;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.xeustechnologies.jcl.JarClassLoader;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
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
        this.jcl = new JarClassLoader(this.getClass().getClassLoader());
    }

    @Override
    public Flux<Object> importPlugin(PluginModel config)
    {
        try
        {
            log.info("Loading plugin jar: {} -- size of jar: {}kb", config.getName(),
                    ((float) config.getFileData().length / 1000.0f));
            long now = System.currentTimeMillis();
            jcl.add(new ByteArrayInputStream(config.getFileData()));
            if ("".equals(config.getClassPath()) || config.getClassPath() == null)
            {
                //Load All
                return Flux.create(sink -> {
                    List<String> check = Arrays.asList("Proxy", "java/lang", "com/sun", "Guice", "google",
                            "GeneratedMethodAccessor", "GeneratedConstructorAccessor", "javax", "discord4j");
                    if (jcl.getLoadedResources().keySet().isEmpty())
                    {
                        log.error("Unable to load plugin: {} no classes found", config.getName());
                    } else
                    {
                        log.info("Loaded plugin jar: {} -- time took: {}ms", config.getName(),
                                System.currentTimeMillis() - now);
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
                                        if (e.getMessage().contains("net/bdavies"))
                                        {
                                            log.error("Failed to load plugin: {} because it is out " +
                                                            "of date with the current server version cannot" +
                                                            " load " +
                                                            "class {} because of no ClassDefinition for {}." +
                                                            " " +
                                                            "either update the plugin to the latest version" +
                                                            " or " +
                                                            "contact the plugin maintainer",
                                                    config.getName(), c, e.getMessage());
                                        } else if (contains(c, e.getMessage(), check))
                                        {
                                            return null;
                                        } else
                                        {
                                            log.error("Failed to load class: " + c +
                                                    ", please contact the plugin maintainer.", e);
                                        }
                                        return null;
                                    }
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .map(c -> (Class<?>) c)
                            .filter(this::isRegistrableBean)
                            .peek(c -> {
                                BabblebotApplication bbApp = application.get(BabblebotApplication.class);
                                GenericApplicationContext applicationContext =
                                        (GenericApplicationContext) bbApp.getApplicationContext();
                                applicationContext.registerBean(c);
                            })
                            .filter(c -> c.isAnnotationPresent(Plugin.class) ||
                                    Arrays.stream(c.getInterfaces()).anyMatch(i -> i == IPlugin.class))
                            .forEach(c -> sink.next(application.get(c)));
                    sink.complete();
                });


            } else
            {
                Class<?> clazz = jcl.loadClass(config.getClassPath());
                return Flux.just(application.get(clazz));
            }
        }
        catch (ClassNotFoundException e)
        {
            log.info("Plugin cannot be found please check your classpath.");
        }

        return Flux.empty();
    }

    private boolean isRegistrableBean(Class<?> c)
    {
        List<Class<? extends Annotation>> registrableAnnotations = List.of(
                Component.class,
                Service.class,
                Plugin.class,
                PluginConfig.class,
                Repository.class,
                Controller.class,
                RestController.class,
                Entity.class
        );

        return Arrays.stream(c.getAnnotations())
                .anyMatch(a -> registrableAnnotations.stream().anyMatch(ac -> ac.equals(a.annotationType())));
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
