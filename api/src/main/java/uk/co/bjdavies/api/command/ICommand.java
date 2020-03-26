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
