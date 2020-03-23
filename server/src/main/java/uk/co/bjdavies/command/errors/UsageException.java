package uk.co.bjdavies.command.errors;

/**
 * This is a usage error if this is thrown then the bot will print out the usage of the command.
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public class UsageException extends Exception {

    public UsageException(String usage) {
        super("Command Invalid! Please follow usage: " + usage);
    }
}
