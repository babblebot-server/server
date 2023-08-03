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

package net.babblebot.api.discord;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.babblebot.api.obj.message.discord.interactions.Row;
import net.babblebot.api.obj.message.discord.interactions.button.Button;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownMenu;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownView;
import org.slf4j.helpers.MessageFormatter;

import java.util.List;

/**
 * Sending spec for a DiscordMessage
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.28
 */
@Data
@Builder(toBuilder = true)
@Jacksonized
@Slf4j
public final class DiscordMessageSendSpec
{
    @Singular("addEmbed")
    private final List<EmbedMessage> embeds;
    @Singular("addDropdownMenu")
    private final List<DropdownMenu> menus;
    @Singular("addButton")
    private final List<Button> buttons;
    @Singular("addRow")
    private final List<Row> row;
    private final DropdownView view;
    @Builder.Default
    private final String content = "";
    @Builder.Default
    private final boolean tts = false;

    public static DiscordMessageSendSpec fromString(String content, Object... args)
    {
        return DiscordMessageSendSpec.builder()
                .content(MessageFormatter.arrayFormat(content, args).getMessage())
                .build();
    }

    public static DiscordMessageSendSpec fromEmbed(EmbedMessage embedMessage)
    {
        return DiscordMessageSendSpec.builder()
                .addEmbed(embedMessage)
                .build();
    }

    public static DiscordMessageSendSpec fromTts(String content)
    {
        return DiscordMessageSendSpec.builder()
                .content(content)
                .tts(true)
                .build();
    }

    public static DiscordMessageSendSpec fromDropdown(DropdownMenu menu)
    {
        return DiscordMessageSendSpec.builder()
                .addDropdownMenu(menu)
                .build();
    }

    public static DiscordMessageSendSpec fromButton(Button button)
    {
        return DiscordMessageSendSpec.builder()
                .addButton(button)
                .build();
    }

    public static DiscordMessageSendSpec fromRow(Row row)
    {
        return DiscordMessageSendSpec.builder()
                .addRow(row)
                .build();
    }

    public static DiscordMessageSendSpec fromDropdownView(DropdownView view)
    {
        return DiscordMessageSendSpec.builder()
                .view(view)
                .build();
    }
}
