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

package net.bdavies;

import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.IApplication;
import net.bdavies.api.command.ICommandDispatcher;
import net.bdavies.api.config.EPluginPermission;
import net.bdavies.api.config.IConfig;
import net.bdavies.api.config.IDiscordConfig;
import net.bdavies.api.config.ISystemConfig;
import net.bdavies.api.discord.IDiscordFacade;
import net.bdavies.api.plugins.IPluginContainer;
import net.bdavies.api.variables.IVariableContainer;
import net.bdavies.config.ConfigFactory;
import net.bdavies.core.CorePlugin;
import net.bdavies.plugins.importing.ImportPluginFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;
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
@ComponentScan({"net.bdavies"})
public final class BabblebotApplication implements IApplication
{
    private static int triedToMake;
    private final IConfig config;
    private final ICommandDispatcher commandDispatcher;
    private final IVariableContainer variableContainer;
    private final IPluginContainer pluginContainer;
    private final ApplicationContext applicationContext;
    private static Class<?> mainClass;
    private static String[] args;
    private String serverVersion;

    /**
     * Create a {@link BabblebotApplication} for BabbleBot
     */
    public BabblebotApplication(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
        log.info("Started Application");
        log.info("Args: " + Arrays.toString(args));
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                log.info("Active Threads: " + Thread.activeCount());
            }
        }, 1000, 1000 * 60 * 60);

        try
        {
            serverVersion = new String(getClass().getResourceAsStream("/version.txt").readAllBytes()).trim();
        }
        catch (IOException e)
        {
            log.error("Could not find version file please run :createProperties", e);
        }

        commandDispatcher = applicationContext.getBean(ICommandDispatcher.class);
        variableContainer = applicationContext.getBean(IVariableContainer.class);
        pluginContainer = applicationContext.getBean(IPluginContainer.class);
        config = ConfigFactory.makeConfig("config.json");
    }

    void initPlugins()
    {
        log.info("Beans: {}", Arrays.toString(applicationContext.getBeanDefinitionNames()));
        pluginContainer.addPlugin("core", get(CorePlugin.class), EPluginPermission.all());

        assert config != null;
        config.getPlugins().forEach(pluginConfig ->
                ImportPluginFactory.importPlugin(pluginConfig, this)
                        .subscribe(pObj -> pluginContainer.addPlugin(pObj,
                                pluginConfig.getPluginPermissions())));
    }


    @Bean
    IDiscordConfig discordConfig()
    {
        log.info("Config: {}", this.config);
        return this.getConfig().getDiscordConfig();
    }

    @Bean
    ISystemConfig systemConfig()
    {
        return this.getConfig().getSystemConfig();
    }

    /**
     * This will make a Application and ensure that only one application can be made.
     *
     * @param args The Arguments passed in by the cli.
     * @return {@link IApplication}
     * @throws RuntimeException if the application tried to made twice
     */
    public static IApplication make(Class<?> mainClass, String[] args)
    {
        return make(mainClass, BabblebotApplication.class, args);
    }

    /**
     * This will make a Application and ensure that only one application can be made.
     * 2.0.0
     *
     * @param args  The Arguments passed in by the cli.
     * @param clazz Bootstrap your own application useful for developing plugins.
     * @return {@link IApplication}
     * @throws RuntimeException if the application tried to made twice
     */
    public static <T extends BabblebotApplication> IApplication make(Class<?> mainClass, Class<T> clazz,
                                                                     String[] args)
    {
        if (triedToMake == 0)
        {
            triedToMake++;
            BabblebotApplication.mainClass = mainClass;
            BabblebotApplication.args = Arrays.copyOf(args, args.length);
            val context = SpringApplication.run(mainClass, args);
            BabblebotApplication app = (BabblebotApplication) context.getBean(IApplication.class);
            app.initPlugins();
            return app;
        } else
        {
            log.error("Tried to create Multiple applications, Exiting...",
                    new RuntimeException("Tried to make multiple Applications"));
            System.exit(-1);
            return null;
        }
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

    @Override
    public String getServerVersion()
    {
        return serverVersion;
    }

    @Override
    public void shutdown(int timeout)
    {
        Executors.newSingleThreadExecutor().submit(() -> {
            Timer timer = new Timer();

            getPluginContainer().shutDownPlugins();
            IDiscordFacade facade = get(IDiscordFacade.class);
            facade.logoutBot().block();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    System.exit(0);
                }
            }, timeout);
        });
    }

    @Override
    public boolean hasArgument(String argument)
    {
        return Arrays.asList(args).contains(argument);
    }

    @Override
    public void restart()
    {
        Executors.newSingleThreadExecutor().submit(() -> {
            getPluginContainer().shutDownPlugins();
            IDiscordFacade facade = get(IDiscordFacade.class);
            facade.registerEventHandler(DisconnectEvent.class, d -> log.info("Bot has been logged out!!!"));
            facade.logoutBot().block();
            try
            {
                List<String> command = new ArrayList<>();

                if (new File("Babblebot").exists())
                {
                    command.add("./Babblebot");
                } else
                {
                    String cmd = System.getProperty("java.home") + File.separator + "bin" + File.separator +
                            "java";
                    command.add(cmd);
                    command.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
                    command.add("-cp");
                    command.add(ManagementFactory.getRuntimeMXBean().getClassPath());
                    command.add(mainClass.getName());
                    command.addAll(Arrays.asList(args));
                }
                command.add("-restart");


                ProcessBuilder pb = new ProcessBuilder();
                log.info("Built command: {} ", command);
                pb.command(command);
                pb.inheritIO();
                Process p = pb.start();
                p.waitFor();
                System.exit(p.exitValue());
            }
            catch (IOException | InterruptedException e) /* NOSONAR */
            {
                log.error("An error occurred when trying to restart Babblebot", e);
            }
        });
    }
}
