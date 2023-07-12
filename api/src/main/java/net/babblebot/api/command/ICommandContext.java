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

package net.babblebot.api.command;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface ICommandContext {
    /**
     * This will return the value of a given parameter.
     *
     * @param name - The name of the paramater
     * @return String
     */
    String getParameter(String name);

    /**
     * This checks whether a parameter is present.
     *
     * @param name - This is the name of the paramater.
     * @return boolean
     */
    boolean hasParameter(String name);

    /**
     * This checks whether a parameter is present.
     *
     * @param name - This is the name of the paramater.
     * @return boolean
     */
    boolean hasNonEmptyParameter(String name);


    /**
     * Returns the command's name.
     *
     * @return String
     */
    String getCommandName();

    /**
     * Returns the value of the command (if any).
     *
     * @return String
     */
    String getValue();

    /**
     * This returns the command's type.
     * Either "Terminal" or "Discord"
     *
     * @return String
     */
    String getType();

    /**
     * This will return a response instance so you can send responses to the discord client.
     *
     * @return ICommandResponse
     */
    ICommandResponse getCommandResponse();
}