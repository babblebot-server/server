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

package net.bdavies.babblebot;

import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.IDiscordFacade;
import net.bdavies.babblebot.api.command.ICommandDispatcher;
import net.bdavies.babblebot.api.config.EPluginPermission;
import net.bdavies.babblebot.api.config.IDiscordConfig;
import net.bdavies.babblebot.api.connect.ConnectQueue;
import net.bdavies.babblebot.api.plugins.IPluginContainer;
import net.bdavies.babblebot.api.variables.IVariableContainer;
import net.bdavies.babblebot.connect.ConnectConfigurationProperties;
import net.bdavies.babblebot.core.CorePlugin;
import net.bdavies.babblebot.discord.DiscordFacade;
import net.bdavies.babblebot.discord.services.Discord4JBotMessageService;
import net.bdavies.babblebot.plugins.PluginModel;
import net.bdavies.babblebot.plugins.PluginModelRepository;
import net.bdavies.babblebot.plugins.importing.ImportPluginFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
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
@ComponentScan(
        includeFilters = {@ComponentScan.Filter(ConnectQueue.class)},
        basePackages = {"net.bdavies.babblebot", "net.bdavies.babblebot.api"}
)
public final class BabblebotApplication implements IApplication
{
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
        if (get(IDiscordConfig.class).getToken().equals(""))
        {
            log.error("Discord token not setup: please visit /v1/config/setup-token");
            return;
        }
        ConnectConfigurationProperties connectConfig = get(ConnectConfigurationProperties.class);
        if (!connectConfig.isUseConnect() || connectConfig.isWorker())
        {
            pluginContainer.addPlugin(get(CorePlugin.class), PluginModel.builder()
                    .name("core")
                    .pluginPermissions(EPluginPermission.all())
                    .namespace("")
                    .build());

            get(PluginModelRepository.class).findAll().forEach(pluginModel ->
                    ImportPluginFactory.importPlugin(pluginModel, get(IApplication.class))
                            .subscribe(pObj -> pluginContainer.addPlugin(pObj, pluginModel)));
        }
        get(Discord4JBotMessageService.class).registerConnectHandler();
    }


    /**
     * This will make an Application and ensure that only one application can be made.
     *
     * @param args The Arguments passed in by the cli.
     * @return {@link IApplication}
     */
    public static IApplication make(Class<?> mainClass, String[] args)
    {
        return make(mainClass, BabblebotApplication.class, args);
    }

    /**
     * This will make an Application and ensure that only one application can be made.
     *
     * @param args  The Arguments passed in by the cli.
     * @param clazz Bootstrap your own application useful for developing plugins.
     * @return {@link IApplication}
     */
    public static <T extends BabblebotApplication> IApplication make(Class<?> mainClass,
                                                                     Class<T> clazz,
                                                                     String[] args)
    {
        BabblebotApplication.mainClass = mainClass;
        BabblebotApplication.args = Arrays.copyOf(args, args.length);
        val context = SpringApplication.run(mainClass, args);
        IApplication app = context.getBean(IApplication.class);
        context.getBean(BabblebotApplication.class).initPlugins();
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

    public boolean hasArgument(String argument)
    {
        return Arrays.asList(args).contains(argument);
    }

    public void restart()
    {
        Executors.newSingleThreadExecutor().submit(() -> {
            getPluginContainer().shutDownPlugins();
            DiscordFacade facade = get(DiscordFacade.class);
            facade.getClient().getEventDispatcher().on(DisconnectEvent.class)
                    .subscribe(d -> log.info("Bot has been logged out!!!"));
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
