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

package net.babblebot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.command.AbstractCommandContext;

import java.util.Map;

/**
 * DiscordCommandContext
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.25
 */
@Slf4j
public class DiscordCommandContext extends AbstractCommandContext
{
    @Getter
    private final DiscordMessage message;

    /**
     * This is the CommandContext Constructor.
     *
     * @param commandName - The name of the command.
     * @param parameters  - THe command's parameters.
     * @param value       - The value of the command (if any).
     * @param type        - The type of the command.
     */
    public DiscordCommandContext(String commandName, Map<String, String> parameters, String value,
                                 String type, DiscordMessage message)
    {
        super(commandName, parameters, value, type);
        this.message = message;
    }
}
