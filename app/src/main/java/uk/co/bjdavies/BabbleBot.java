package uk.co.bjdavies;

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
        Application.make(BabbleBot.class, args);
    }
}
