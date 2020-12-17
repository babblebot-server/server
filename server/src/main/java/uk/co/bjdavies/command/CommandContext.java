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

package uk.co.bjdavies.command;


import discord4j.core.object.entity.Message;
import lombok.extern.log4j.Log4j2;
import net.bdavies.api.command.ICommandContext;
import net.bdavies.api.command.ICommandResponse;
import net.bdavies.api.discord.IDiscordCommandUtil;
import uk.co.bjdavies.discord.DiscordCommandUtil;

import java.util.Map;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public class CommandContext implements ICommandContext {
    /**
     * This is the Map for all the command's paramaters.
     */
    private final Map<String, String> parameters;


    /**
     * This is the name of the command.
     */
    private final String commandName;


    /**
     * This is the value of the command (if any.)
     */
    private final String value;


    /**
     * This is the type of command (Terminal, Discord)
     */
    private final String type;

    /**
     * This is used for discord messages.
     */
    private Message message;

    /**
     * This is the response system for babblebot.
     */
    private final ICommandResponse commandResponse;


    /**
     * This is the CommandContext Constructor.
     *
     * @param commandName - The name of the command.
     * @param parameters  - THe command's parameters.
     * @param value       - The value of the command (if any).
     * @param type        - The type of the command.
     */
    public CommandContext(String commandName, Map<String, String> parameters, String value, String type) {
        this.commandName = commandName;
        this.parameters = parameters;
        this.value = value;
        this.type = type;
        commandResponse = new CommandResponse();
    }


    /**
     * This is the CommandContext Constructor.
     *
     * @param commandName - The name of the command.
     * @param parameters  - THe command's parameters.
     * @param value       - The value of the command (if any).
     * @param type        - The type of the command.
     * @param message     - IMessage of which was created when the message was sent.
     */
    public CommandContext(String commandName, Map<String, String> parameters, String value, String type, Message message) {
        this.commandName = commandName;
        this.parameters = parameters;
        this.value = value;
        this.type = type;
        this.message = message;
        commandResponse = new CommandResponse();
    }


    /**
     * This will return the value of a given parameter.
     *
     * @param name - The name of the paramater
     * @return String
     */
    public String getParameter(String name) {
        if (!parameters.containsKey(name)) {
            log.error("Parameter not found.");
        } else {
            return parameters.get(name);
        }
        return "";
    }


    /**
     * This checks whether a parameter is present.
     *
     * @param name - This is the name of the paramater.
     * @return boolean
     */
    public boolean hasParameter(String name) {
        return parameters.containsKey(name);
    }

    @Override
    public boolean hasNonEmptyParameter(String name) {
        return hasParameter(name) && !getParameter(name).equals("");
    }


    /**
     * Returns the command's name.
     *
     * @return String
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Returns the value of the command (if any).
     *
     * @return String
     */
    public String getValue() {
        return value;
    }

    /**
     * This returns the command's type.
     *
     * @return String
     */
    public String getType() {
        return type;
    }

    @Override
    public IDiscordCommandUtil getCommandUtils() {
        return new DiscordCommandUtil(this);
    }

    /**
     * This will return the message object.
     *
     * @return IMessage
     */
    public Message getMessage() {
        return message;
    }

    @Override
    public ICommandResponse getCommandResponse() {
        return commandResponse;
    }
}