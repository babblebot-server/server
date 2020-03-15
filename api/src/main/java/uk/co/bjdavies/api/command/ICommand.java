package uk.co.bjdavies.api.command;

import uk.co.bjdavies.api.IApplication;

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
     * @return String
     */
    String run(IApplication application, ICommandContext commandContext);


    /**
     * This is to make sure that the command the user inputted is valid.
     *
     * @param commandContext - The command context for all command parameters and values.
     * @return boolean
     */
    boolean validateUsage(ICommandContext commandContext);
}
