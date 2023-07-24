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
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.IApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.util.Arrays;

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
@ComponentScan(basePackages = {"net.babblebot", "net.babblebot.api"})
public final class BabblebotApplication implements IApplication
{
    private static Class<?> mainClass;
    private static ConfigurableApplicationContext mainContext;
    private static String[] args;
    private final ApplicationContext applicationContext;

    /**
     * Create a {@link BabblebotApplication} for BabbleBot
     */
    public BabblebotApplication(GenericApplicationContext applicationContext)
    {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        this.applicationContext = applicationContext;
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
        mainContext = context;
        return app;
    }

    /**
     * This will return of an instance of a class and inject any dependencies that are inside the dependency
     * injector.
     *
     * @param clazz - this is the class to create
     * @return Object this is an object of type T
     */
    public <T> T get(Class<T> clazz)
    {
        return applicationContext.getBean(clazz);
    }

    public boolean hasArgument(String argument)
    {
        return Arrays.asList(args).contains(argument);
    }
}
