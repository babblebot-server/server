/*
 * MIT License
 *
 * Copyright (c) 2020 Ben Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.babblebot.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.IApplication;
import net.babblebot.api.command.*;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownView;
import net.babblebot.api.plugins.Plugin;
import net.babblebot.core.command.*;
import net.babblebot.core.repository.IgnoreRepository;
import net.babblebot.discord.DiscordCommandContext;
import org.springframework.stereotype.Component;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.27
 */
@Slf4j
@Plugin(author = "Ben Davies <me@bdavies.net>")
@Component
@RequiredArgsConstructor
public class CorePlugin
{
    private final IgnoreRepository ignoreRepository;
    private final ListenCommand listenCommand;
    private final IgnoreCommand ignoreCommand;
    private final RegisterAnnouncementChannelCommand registerAnnouncementChannelCommand;
    private final RemoveAnnouncementChannelCommand removeAnnouncementChannelCommand;
    private final HelpCommand helpCommand;
    private final IApplication application;

    @CommandMiddleware(global = true)
    public boolean checkIfChannelIgnored(ICommandContext ctx)
    {
        if (ctx instanceof DiscordCommandContext discordCommandContext)
        {
            log.info("Checking if channel {} is ignored by babblebot", discordCommandContext
                    .getMessage().getChannel().getName());
            return ignoreRepository
                    .findByChannel((discordCommandContext).getMessage().getChannel())
                    .isEmpty() || "listen".equals(ctx.getCommandName());
        }

        return true;
    }

    @Command(description = "Start listening again to a channel the bot will then start responding again.")
    public String listen(DiscordMessage message, ICommandContext ctx)
    {
        return listenCommand.exec(message, ctx);
    }

    @Command(description = "Ignore a channel so the bot wont respond")
    public String ignore(DiscordMessage message, ICommandContext ctx)
    {
        return ignoreCommand.exec(message, ctx);
    }

    @Command(aliases = {"register-announcement-channel",
            "register-ac"},
            description = "Register the channel where the command is ran as a announcement channel")
    public String register(ICommandContext commandContext, DiscordMessage message)
    {
        return registerAnnouncementChannelCommand.exec(message, commandContext);
    }

    @Command(aliases = {"remove-announcement-channel",
            "remove-ac"},
            description = "Remove the channel where the command is ran as a announcement channel")
    public String remove(ICommandContext commandContext, DiscordMessage message)
    {
        return removeAnnouncementChannelCommand.exec(message, commandContext);
    }

    @Command(description = "This will help you to discover all the commands and their features.",
            type = CommandType.ALL,
            exampleValue = "ignore")
    @CommandParam(value = "cmd", canBeEmpty = false,
            exampleValue = "ignore")
    public DropdownView help(ICommandContext commandContext, DiscordMessage message)
    {
        return helpCommand.exec(message, commandContext);
    }
}
