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

package net.babblebot.discord.obj.factories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.command.CommandParam;
import net.babblebot.api.command.ICommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

/**
 * Factory for converting between babblebot and internal apis
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.29
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class DiscordCommandFactory
{
    private final JDA client;

    public SlashCommandData createSlashCommand(String namespace, ICommand command)
    {
        SlashCommandData commandData =
                Commands.slash(namespace + command.getAliases()[0].toLowerCase(Locale.ROOT),
                        command.getDescription());

        Arrays.stream(command.getCommandParams()).forEach(commandParam -> {
            //TODO: Support autocomplete options
            if (commandParam.canBeEmpty())
            {
                commandData.addOption(OptionType.BOOLEAN, commandParam.value(), getDescription(commandParam),
                        !commandParam.optional());
            } else
            {
                commandData.addOption(OptionType.STRING, commandParam.value(), getDescription(commandParam),
                        !commandParam.optional());
            }
        });

        commandData.setGuildOnly(true);
        commandData.setNSFW(false);

        //TODO: Support NSFW, i18n, etc..

        return commandData;
    }

    private String getDescription(CommandParam commandParam)
    {
        if ("".equals(commandParam.description()))
        {
            return commandParam.value();
        }
        return commandParam.description();
    }
}
