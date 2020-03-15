package uk.co.bjdavies.api.discord;

/**
 * This is a list of utils attached to a command context and they make it easier to do things inside commands.
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IDiscordCommandUtil {
    void sendPrivateMessage(String text);
}
