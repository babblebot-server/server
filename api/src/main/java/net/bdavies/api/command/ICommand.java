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

package net.bdavies.api.command;

import net.bdavies.api.IApplication;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface ICommand {
    /**
     * The aliases of the command.
     *
     * @return String[]
     */
    String[] getAliases();

    /**
     * This will return the commands examples
     *
     * @return String
     */
    String[] getExamples();

    /**
     * The Description for the command.
     *
     * @return String
     */
    String getDescription();

    /**
     * The Usage for the command.
     *
     * @return String
     */
    String getUsage();


    /**
     * The type of command (Terminal, Discord, All).
     *
     * @return String
     */
    String getType();


    /**
     * This is the execution point for the command.
     *
     * @param application    - The application instance.
     * @param commandContext - The command context for all command parameters and values.
     * @return String - This return method is deprecated. use {@link ICommandResponse}
     * @deprecated - To be removed in 2.0.0
     */
    String run(IApplication application, ICommandContext commandContext);

    /**
     * This is the execution point for the command.
     *
     * @param application    - The application instance.
     * @param commandContext - The command context for all command parameters and values.
     * @since 1.2.7
     */
    void exec(IApplication application, ICommandContext commandContext);


    /**
     * This is to make sure that the command the user inputted is valid.
     *
     * @param commandContext - The command context for all command parameters and values.
     * @return boolean
     */
    boolean validateUsage(ICommandContext commandContext);
}
