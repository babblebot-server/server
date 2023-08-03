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

package net.babblebot.discord.obj.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.obj.message.discord.DiscordChannel;
import net.babblebot.api.obj.message.discord.DiscordId;
import net.babblebot.discord.obj.factories.DiscordChannelFactory;

/**
 * DB Converter for Text Channel
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.29
 */
@Slf4j
@Converter(autoApply = true)
@RequiredArgsConstructor
public class TextChannelConverter implements AttributeConverter<DiscordChannel, String>
{
    private final DiscordIdConverter discordIdConverter = new DiscordIdConverter();
    private final DiscordChannelFactory discordChannelFactory;

    @Override
    public String convertToDatabaseColumn(DiscordChannel attribute)
    {
        if (attribute == null)
        {
            return null;
        }
        return discordIdConverter.convertToDatabaseColumn(attribute.getGuildId())
                + ":" + discordIdConverter.convertToDatabaseColumn(attribute.getId());
    }

    @Override
    public DiscordChannel convertToEntityAttribute(String dbData)
    {
        DiscordId guildId = discordIdConverter.convertToEntityAttribute(dbData.split(":")[0]);
        DiscordId channelId = discordIdConverter.convertToEntityAttribute(dbData.split(":")[1]);
        return discordChannelFactory.makeFromId(channelId.toLong()).orElse(null);
    }
}
