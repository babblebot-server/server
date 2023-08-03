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

package net.babblebot.discord.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.config.IDiscordConfig;
import net.babblebot.command.execution.CommandExecutionSpec;
import net.babblebot.command.execution.CommandExecutor;
import net.babblebot.command.parser.DiscordMessageParser;
import net.babblebot.command.renderer.DiscordInteractionEventRenderer;
import net.babblebot.discord.obj.factories.DiscordMessageFactory;
import net.babblebot.discord.services.DiscordMessagingService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class SlashCommandListener extends ListenerAdapter
{
    private final IDiscordConfig config;
    private final DiscordMessageFactory messageFactory;
    private final DiscordMessagingService messagingService;
    private final CommandExecutor commandExecutor;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        SlashCommandInteraction m = event.getInteraction();
        var guild = m.getGuild();
        var channel = m.getChannel().asTextChannel();
        var author = m.getUser();
        var msgId = m.getIdLong();
        var msgContent = new StringBuilder(String.join("-", m.getFullCommandName().split(" ")));
        m.getOptions().forEach(optionMapping -> {
            msgContent.append(" -")
                    .append(optionMapping.getName());
            if (!optionMapping.getAsString().isEmpty())
            {
                msgContent.append("=")
                        .append("\"")
                        .append(optionMapping.getAsString())
                        .append("\"");
            }
        });

        val discordMessage = messageFactory.makeFromInternal(guild, channel, author, msgId,
                msgContent.toString());
        val messageParser = new DiscordMessageParser(discordMessage);
        val renderer = new DiscordInteractionEventRenderer(discordMessage, messagingService, event);

        commandExecutor.executeCommand(CommandExecutionSpec.builder()
                .onPreExecution((ctx, cmd) -> event.deferReply().complete())
                .messageParser(messageParser)
                .message(msgContent.toString())
                .commandRenderer(renderer)
                .build());
    }
}
