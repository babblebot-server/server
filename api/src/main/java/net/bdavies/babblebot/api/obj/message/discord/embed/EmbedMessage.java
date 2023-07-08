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

package net.bdavies.babblebot.api.obj.message.discord.embed;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.babblebot.api.obj.DiscordColor;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * Embedded Message Dto as a Response
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.23
 */
@Slf4j
@Data
@Builder(toBuilder = true)
@Jacksonized
public class EmbedMessage
{
    @Builder.Default
    private String title = null;
    @Builder.Default
    private String description = null;
    @Builder.Default
    private String url = null;
    @Builder.Default
    private Instant timestamp = null;
    @Builder.Default
    private DiscordColor color = null;
    @Builder.Default
    private EmbedFooter footer = null;
    @Builder.Default
    private EmbedAuthor author = null;
    @Builder.Default
    private String image = null;
    @Builder.Default
    private String thumbnail = null;
    @Singular("addField")
    private List<EmbedField> fields;

    public void addField(String name, String value, boolean inline)
    {
        if (fields == null)
        {
            fields = new LinkedList<>();
        } else
        {
            fields = new LinkedList<>(fields);
        }
        fields.add(EmbedField.builder()
                .name(name)
                .value(value)
                .inline(inline)
                .build());
    }
}
