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

package net.bdavies.command.parser;

import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.command.ICommandContext;
import net.bdavies.command.CommandContext;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class DiscordMessageParser implements MessageParser {
    /**
     * This is the message that was created when the message was sent.
     */
    private final Message message;

    /**
     * This will construct the class.
     *
     * @param message - The IMessage which was created when the message was sent.
     */
    public DiscordMessageParser(Message message) {
        this.message = message;
    }


    /**
     * This will parse the string inputted the by the user.
     *
     * @param message - The raw inputted message.
     * @return CommandContext
     */
    @Override
    public ICommandContext parseString(String message) {
        return new CommandContext(parseCommandName(message).toLowerCase(), parseParams(message), parseValue(message), "Discord", this.message);
    }


    /**
     * This will parse the value of the command if there is one.
     *
     * @param message - The raw inputted message.
     * @return String
     */
    private String parseValue(String message) {
        Matcher matcher = getParameterMatcher(message);
        message = message.replace(parseCommandName(message), "");
        while (matcher.find()) {
            message = message.replace(matcher.group().trim(), "");
        }


        matcher = getRawParameters(message);
        while (matcher.find()) {
            message = message.replace(matcher.group().trim(), "");
        }


        message = message.trim();
        return message;
    }


    /**
     * This will parse the command name from the message.
     *
     * @param message - The raw inputted message.
     * @return String
     */
    private String parseCommandName(String message) {
        int indexOfFirstSpace = message.indexOf(" ");

        if (indexOfFirstSpace == -1) {
            return message;
        } else {
            return message.substring(0, indexOfFirstSpace).trim();
        }
    }


    /**
     * This will parse the parameters from the inputted message
     *
     * @param message - The raw inputted message.
     * @return Map(String, String)
     */
    private Map<String, String> parseParams(String message) {
        Map<String, String> params = new HashMap<>();
        Matcher matcher = getParameterMatcher(message);
        String copy = new String(message.getBytes());
        while (matcher.find()) {
            String name = matcher.group(1).trim();
            String value = matcher.group(2).replaceAll("\"", "").trim();
            params.put(name, value);
            copy = copy.replace(matcher.group().trim(), "");
        }

        matcher = getRawParameters(copy);

        while (matcher.find()) {
            String name = matcher.group(1).trim();
            params.put(name, "");
        }

        return params;
    }


    /**
     * This will parse the parameters in the message and return a Matcher.
     *
     * @param message - The raw inputted message.
     * @return Matcher
     */
    private Matcher getParameterMatcher(String message) {
        String parameterRegex = " -([a-zA-Z0-9]+)=(([a-zA-Z0-9:/?=&_.\\-]+)|(\"([a-zA-Z0-9:/?=&_.\\-]+)\"))";

        Pattern pattern = Pattern.compile(parameterRegex);

        return pattern.matcher(message);
    }

    private Matcher getRawParameters(String message) {
        String parameterRegex = " -([a-zA-Z0-9]+)";

        Pattern pattern = Pattern.compile(parameterRegex);

        return pattern.matcher(message);
    }
}
