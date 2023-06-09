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

package net.bdavies.babblebot.api.obj.message.discord;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * An Object for the DiscordId
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.10
 */
@Slf4j
@Data
@Builder
@Jacksonized
public class DiscordId implements Serializable
{
    @Getter(AccessLevel.PRIVATE)
    private final long id;

    /**
     * Create a DiscordId instance from an unsigned long
     *
     * @param id - unsigned long
     * @return DiscordId Instance
     */
    public static DiscordId from(long id)
    {
        return DiscordId.builder().id(id).build();
    }

    /**
     * Create a DiscordId instance from an unsigned long string
     *
     * @param id - unsigned long string
     * @return DiscordId Instance
     */
    public static DiscordId from(String id)
    {
        if (id == null)
        {
            return from(0);
        }
        return from(Long.parseUnsignedLong(id));
    }

    /**
     * Return the id as an unsigned long
     *
     * @return the id
     */
    public long toLong()
    {
        return id;
    }

    /**
     * Return the string value of the discord id
     *
     * @return unsigned long
     */
    public String toString()
    {
        return Long.toUnsignedString(id);
    }
}
