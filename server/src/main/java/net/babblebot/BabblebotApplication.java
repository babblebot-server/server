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

package net.babblebot;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.IApplication;
import net.babblebot.api.command.ICommandDispatcher;
import net.babblebot.api.config.EPluginPermission;
import net.babblebot.api.config.IDiscordConfig;
import net.babblebot.api.connect.ConnectQueue;
import net.babblebot.api.events.PluginAddedEvent;
import net.babblebot.api.events.PluginRemovedEvent;
import net.babblebot.api.events.ShutdownEvent;
import net.babblebot.api.plugins.IPluginContainer;
import net.babblebot.api.variables.IVariableContainer;
import net.babblebot.connect.ConnectConfigurationProperties;
import net.babblebot.core.CorePlugin;
import net.babblebot.discord.services.Discord4JBotMessageService;
import net.babblebot.events.EventDispatcher;
import net.babblebot.plugins.PluginModel;
import net.babblebot.plugins.PluginModelRepository;
import net.babblebot.plugins.importing.ImportPluginFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 * Main Application Class for BabbleBot where all the services and providers get executed.
 * <p>
 * When developing plugins be sure to extend this app and override loadPlugins and include your plugin for
 * testing.
 * Please see github wiki for more information.
 * </p>
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */

@Slf4j
@Component
@Getter
@ComponentScan(includeFilters = {@ComponentScan.Filter(ConnectQueue.class)},
        basePackages = {"net.bdavies.babblebot", "net.bdavies.babblebot.api"})
public final class BabblebotApplication implements IApplication
{
    private static Class<?> mainClass;
    private static ConfigurableApplicationContext mainContext;
    private static String[] args;

    private final ICommandDispatcher commandDispatcher;
    private final IVariableContainer variableContainer;
    private final IPluginContainer pluginContainer;
    private final ApplicationContext applicationContext;
    private String serverVersion;

    /**
     * Create a {@link BabblebotApplication} for BabbleBot
     */
    public BabblebotApplication(GenericApplicationContext applicationContext,
                                ConnectConfigurationProperties connectConfigurationProperties)
    {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        this.applicationContext = applicationContext;
        log.info("Started Application in {} mode",
                connectConfigurationProperties.isLeader() ? "leader" : "worker");
        log.info("Args: " + Arrays.toString(args));

        try
        {
            serverVersion = IOUtils.resourceToString("/version.txt", StandardCharsets.UTF_8).trim();
        }
        catch (IOException e)
        {
            log.error("Could not find version file please run :createProperties", e);
        }

        commandDispatcher = applicationContext.getBean(ICommandDispatcher.class);
        variableContainer = applicationContext.getBean(IVariableContainer.class);
        pluginContainer = applicationContext.getBean(IPluginContainer.class);
    }

    @SneakyThrows
    public void initPlugins()
    {
        if ("".equals(get(IDiscordConfig.class).getToken()))
        {
            log.error("Discord token not setup: please visit /v1/config/setup-token");
            return;
        }
        ConnectConfigurationProperties connectConfig = get(ConnectConfigurationProperties.class);
        if (!connectConfig.isUseConnect() || connectConfig.isWorker())
        {
            pluginContainer.addPlugin(get(CorePlugin.class),
                    PluginModel.builder().name("core").pluginPermissions(EPluginPermission.all())
                            .namespace("").build());

            val plugins = get(PluginModelRepository.class).findAll();
            EventDispatcher dispatcher = get(EventDispatcher.class);

            plugins.forEach(pluginModel -> {
                ImportPluginFactory.importPlugin(pluginModel, get(IApplication.class))
                        .subscribe(pObj -> pluginContainer.addPlugin(pObj, pluginModel));
                dispatcher.dispatchInternal(new PluginAddedEvent(pluginModel), false);
            });

            registerPluginEventListeners();
        }

        registerEventListeners();
        get(Discord4JBotMessageService.class).registerConnectHandler();
    }

    private void registerEventListeners()
    {
        EventDispatcher dispatcher = get(EventDispatcher.class);
        dispatcher.onPriority(ShutdownEvent.class, e -> {
            if (e.isRestart())
            {
                restartInternal();
            } else
            {
                shutdownInternal();
            }
        });
    }

    private void registerPluginEventListeners()
    {
        EventDispatcher dispatcher = get(EventDispatcher.class);
        dispatcher.onPriority(PluginAddedEvent.class, (pae -> {
            if (!pluginContainer.doesPluginExist(pae.getPluginModel().getName()))
            {
                ImportPluginFactory.importPlugin(pae.getPluginModel(), get(IApplication.class))
                        .subscribe(pObj -> pluginContainer.addPlugin(pObj, pae.getPluginModel()));
            }
        }));

        dispatcher.onPriority(PluginRemovedEvent.class, pre -> {
            if (pluginContainer.doesPluginExist(pre.getName()))
            {
                pluginContainer.removePlugin(pre.getName());
            }
        });

    }

    /**
     * This will make an Application and ensure that only one application can be made.
     *
     * @param args The Arguments passed in by the cli.
     * @return {@link IApplication}
     */
    public static IApplication make(Class<?> mainClass,
                                    String[] args)
    {
        BabblebotApplication.mainClass = mainClass;
        BabblebotApplication.args = Arrays.copyOf(args, args.length);
        val context = SpringApplication.run(mainClass, args);
        IApplication app = context.getBean(IApplication.class);
        context.getBean(BabblebotApplication.class).initPlugins();
        mainContext = context;
        return app;
    }

    /**
     * This will return of a instance of a class and inject any dependencies that are inside the dependency
     * injector.
     *
     * @param clazz - this is the class to create
     * @return Object this is an object of type T
     */
    public <T> T get(Class<T> clazz)
    {
        return applicationContext.getBean(clazz);
    }

    public String getServerVersion()
    {
        return serverVersion;
    }

    public void shutdown()
    {
        get(EventDispatcher.class).dispatch(new ShutdownEvent(false));
    }

    public void shutdownInternal()
    {
        Executors.newSingleThreadExecutor().submit(() -> mainContext.close());
    }

    public boolean hasArgument(String argument)
    {
        return Arrays.asList(args).contains(argument);
    }

    public void restart()
    {
        get(EventDispatcher.class).dispatch(new ShutdownEvent(true));
    }

    public void restartInternal()
    {
        Executors.newSingleThreadExecutor().submit(() -> {
            mainContext.close();
            make(mainClass, args);
        });
    }
}
