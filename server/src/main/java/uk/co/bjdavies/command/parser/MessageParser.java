package uk.co.bjdavies.command.parser;

import uk.co.bjdavies.api.command.ICommandContext;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface MessageParser {
    /**
     * This will parse the user's input.
     *
     * @param message - The raw inputted message.
     * @return CommandContext
     */
    ICommandContext parseString(String message);
}
