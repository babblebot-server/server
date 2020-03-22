package uk.co.bjdavies.api.command;

import discord4j.core.object.entity.Message;
import uk.co.bjdavies.api.discord.IDiscordCommandUtil;

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

    IDiscordCommandUtil getCommandUtils();

    /**
     * This will return the message object.
     *
     * @return IMessage
     */
    Message getMessage();

    /**
     * This will return a response instance so you can send responses to the discord client.
     *
     * @return ICommandResponse
     */
    ICommandResponse getCommandResponse();
}
