package uk.co.bjdavies.api.command;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface ICommandMiddleware {
    boolean onExecute(ICommandContext context);
}
