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

package net.babblebot.api.obj.message.discord.interactions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.obj.message.discord.interactions.button.Button;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownMenu;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Row implements InteractionItem
{
    @Singular("addDropdownMenu")
    private final List<DropdownMenu> menus;
    @Singular("addButton")
    private final List<Button> buttons;

    public static Row from(InteractionItem... items)
    {
        return from(Arrays.asList(items));
    }

    public static Row from(Collection<? extends InteractionItem> items)
    {
        RowBuilder builder = Row.builder();
        items.forEach(i -> {
            if (i instanceof DropdownMenu)
            {
                builder.addDropdownMenu((DropdownMenu) i);
            }

            if (i instanceof Button)
            {
                builder.addButton((Button) i);
            }
        });
        return builder.build();
    }
}
