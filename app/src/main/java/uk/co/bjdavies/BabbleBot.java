package uk.co.bjdavies;

import reactor.core.publisher.Flux;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.command.ICommand;
import uk.co.bjdavies.api.command.ICommandContext;

/**
 * A Server for the Discord App this server provides a way for the user to give commands
 * and then this bot acts on those commands
 *
 * @author Ben Davies
 * @see uk.co.bjdavies.Application
 */
public final class BabbleBot {

    /**
     * Constructs a {@code BabbleBot}
     * <p>
     * This constructor is <strong>not</strong> used as this is the entry point for the server.
     */
    private BabbleBot() {
    }

    /**
     * BabbleBot Server Entry point
     *
     * @param args the {@link String} passed in by the command line.
     */
    public static void main(final String[] args) {
        IApplication application = Application.make(args);
        application.getCommandDispatcher().addCommand("bb", new ICommand() {
            @Override
            public String[] getAliases() {
                return new String[]{"test"};
            }

            @Override
            public String getDescription() {
                return "Test Command";
            }

            @Override
            public String getUsage() {
                return "bbtest -param";
            }

            @Override
            public String getType() {
                return "Discord";
            }

            @Override
            public String run(IApplication application, ICommandContext commandContext) {
                return "";
            }

            @Override
            public void exec(IApplication application, ICommandContext commandContext) {
//                commandContext.getCommandResponse()
//                        .sendString(Mono.just("Test from the command dispatcher, $(getRandomGIF(1.14))"));
//
//                commandContext.getCommandResponse()
//                        .sendString("Also, babblebot can have multiple responses for commands now?");

                commandContext.getCommandResponse().sendString(Flux.just("$(getRandomGIF(1.14))",
                        "$(getRandomGIF(1.14))", "$(getRandomGIF(1.14))", "$(getRandomGIF(1.14))"));
            }

            @Override
            public boolean validateUsage(ICommandContext commandContext) {
                return commandContext.hasParameter("param");
            }
        });
    }
}
