package uk.co.bjdavies.command;

import com.google.inject.AbstractModule;
import lombok.Getter;
import uk.co.bjdavies.api.command.ICommandDispatcher;

/**
 * This is a module class that allows to inject command stuff into a class when using {@link com.google.inject.Inject}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class CommandModule extends AbstractModule {
    @Getter
    private final ICommandDispatcher commandDispatcher;


    public CommandModule() {
        this.commandDispatcher = new CommandDispatcher();
    }

    @Override
    protected void configure() {
        bind(ICommandDispatcher.class).toInstance(this.commandDispatcher);
    }

}
