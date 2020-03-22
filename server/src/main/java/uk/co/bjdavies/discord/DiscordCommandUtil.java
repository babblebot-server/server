package uk.co.bjdavies.discord;

import discord4j.core.object.entity.Member;
import uk.co.bjdavies.api.command.ICommandContext;
import uk.co.bjdavies.api.discord.IDiscordCommandUtil;

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
        commandContext.getMessage().getAuthorAsMember()
                .flatMap(Member::getPrivateChannel)
                .flatMap(c -> c.createMessage(text))
                .subscribe();
    }
}
