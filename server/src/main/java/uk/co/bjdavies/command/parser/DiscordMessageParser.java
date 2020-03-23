package uk.co.bjdavies.command.parser;

import discord4j.core.object.entity.Message;
import uk.co.bjdavies.api.command.ICommandContext;
import uk.co.bjdavies.command.CommandContext;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
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
            message = message.replace(matcher.group(), "");
        }

        matcher = getRawParameters(message);
        while (matcher.find()) {
            message = message.replace(matcher.group(), "");
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
            String name = matcher.group(1);
            String value = matcher.group(2).replaceAll("\"", "");
            params.put(name, value);
            copy = copy.replace(matcher.group(), "");
        }

        matcher = getRawParameters(copy);

        while (matcher.find()) {
            String name = matcher.group(1);
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
        String parameterRegex = "-([a-zA-Z0-9]+)=(([a-zA-Z0-9:/?=&_.]+)|(\"([a-zA-Z0-9:/?=&_.]+)\"))";

        Pattern pattern = Pattern.compile(parameterRegex);

        return pattern.matcher(message);
    }

    private Matcher getRawParameters(String message) {
        String parameterRegex = "-([a-zA-Z0-9]+)";

        Pattern pattern = Pattern.compile(parameterRegex);

        return pattern.matcher(message);
    }
}
