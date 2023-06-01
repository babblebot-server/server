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

package net.bdavies.db.model.serialization.util;

import com.google.inject.Inject;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.discord.IDiscordFacade;
import net.bdavies.db.model.IModelProperty;
import net.bdavies.db.model.Model;
import net.bdavies.db.model.serialization.ISQLSerializationObject;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
public class TextChannelSerializationObject implements ISQLSerializationObject<Model, TextChannel>
{
    private final SnowflakeSerializationObject snowflakeSerializationObject =
            new SnowflakeSerializationObject();
    private final IDiscordFacade facade;

    @Inject
    public TextChannelSerializationObject(IDiscordFacade facade)
    {
        this.facade = facade;
    }

    @SneakyThrows
    @Override
    public TextChannel deserialize(Model model, String data, IModelProperty property)
    {
        Snowflake guildId = snowflakeSerializationObject.deserialize(model, data.split(":")[0], property);
        Snowflake channelId = snowflakeSerializationObject.deserialize(model, data.split(":")[1], property);
        return Utilities.monoBlock(facade.getClient()
                .getGuildById(guildId)
                .flatMap(g -> g.getChannelById(channelId))
                .cast(TextChannel.class));
    }

    @SneakyThrows
    @Override
    public String serialize(Model model, TextChannel data, IModelProperty property)
    {
        if (data == null)
        {
            return null;
        } else
        {
            return snowflakeSerializationObject.serialize(model, data.getGuildId(), property)
                    + ":" + snowflakeSerializationObject.serialize(model, data.getId(), property);
        }
    }

    @Override
    public String getSQLColumnType()
    {
        return "VARCHAR(129)";
    }
}
