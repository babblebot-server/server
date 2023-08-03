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

package net.babblebot.api.obj.message.discord.interactions.button;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.discord.DiscordMessageSendSpec;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.api.obj.message.discord.interactions.InteractionItem;

import java.util.function.Function;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.30
 */
@Slf4j
@Data
@Builder(toBuilder = true)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Button implements InteractionItem
{
    private ButtonType type;
    private final String label;
    private final String link;
    @Builder.Default
    private final boolean disableOnClick = true;
    @Builder.Default
    private final Function<DiscordMessage, DiscordMessageSendSpec> onClick = msg -> DiscordMessageSendSpec.fromString(
            "Please supply a click handler");

    public static Button primary(String label, Function<DiscordMessage, DiscordMessageSendSpec> onClick)
    {
        return Button.builder()
                .type(ButtonType.PRIMARY)
                .onClick(onClick)
                .label(label).build();
    }

    public static Button success(String label, Function<DiscordMessage, DiscordMessageSendSpec> onClick)
    {
        return Button.builder().type(ButtonType.SUCCESS)
                .onClick(onClick)
                .label(label).build();
    }

    public static Button secondary(String label, Function<DiscordMessage, DiscordMessageSendSpec> onClick)
    {
        return Button.builder().type(ButtonType.SECONDARY)
                .onClick(onClick)
                .label(label).build();
    }

    public static Button danger(String label, Function<DiscordMessage, DiscordMessageSendSpec> onClick)
    {
        return Button.builder().type(ButtonType.DANGER)
                .onClick(onClick)
                .label(label).build();
    }

    public static Button link(String label, String link)
    {
        return Button.builder().type(ButtonType.LINK).label(label).link(link).build();
    }
}
