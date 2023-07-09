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

package net.babblebot.api;

import net.babblebot.api.plugins.IPluginContainer;
import net.babblebot.api.command.ICommandDispatcher;
import net.babblebot.api.variables.IVariableContainer;
import org.springframework.stereotype.Component;

/**
 * This is a interface for an application
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Component
public interface IApplication
{

    /**
     * This will return of a instance of a class and inject any dependencies that are inside the dependency
     * injector.
     *
     * @param clazz - this is the class to create
     * @return {@link Object<T>} this is an object of type T
     */
    <T> T get(Class<T> clazz);

    /**
     * This will return an instance of the application command dispatcher.
     *
     * @return {@link ICommandDispatcher}
     */
    ICommandDispatcher getCommandDispatcher();

    /**
     * This will return an instance of the application variable container
     *
     * @return {@link IVariableContainer}
     */
    IVariableContainer getVariableContainer();

    /**
     * This will return an instance of the application plugin container
     *
     * @return {@link IVariableContainer}
     */
    IPluginContainer getPluginContainer();

    /**
     * This will return the version of the server.
     *
     * @return String
     */
    String getServerVersion();

    /**
     * Will shut down the application
     */
    void shutdown();

    /**
     * This will return true if babblebot has a argument basic at the moment only supports empty arguments
     * e.g. -restart
     *
     * @param argument - argument to test for
     * @return boolean
     */
    boolean hasArgument(String argument);

    /**
     * Restart babblebot
     */
    void restart();
}
