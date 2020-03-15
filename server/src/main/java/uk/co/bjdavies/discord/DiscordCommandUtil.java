package uk.co.bjdavies.discord;

import uk.co.bjdavies.api.command.ICommandContext;
import uk.co.bjdavies.api.discord.IDiscordCommandUtil;

import java.util.Objects;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class DiscordCommandUtil implements IDiscordCommandUtil {

    private final ICommandContext commandContext;

    public DiscordCommandUtil(ICommandContext commandContext) {
        this.commandContext = commandContext;
    }

    @Override
    public void sendPrivateMessage(String text) {
        Objects.requireNonNull(Objects.requireNonNull(commandContext.getMessage().getAuthorAsMember().block())
                .getPrivateChannel().block()).createMessage(text).block();
    }
}
