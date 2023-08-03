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
import net.babblebot.api.obj.message.discord.DiscordChannel;
import net.babblebot.api.obj.message.discord.DiscordId;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Factory for converting between babblebot and internal apis
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.26
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class DiscordChannelFactory implements IDiscordObjectFactory<DiscordChannel, TextChannel>
{
    private final JDA client;
    private final DiscordGuildFactory discordGuildFactory;

    @Override
    public DiscordChannel makeFromInternal(TextChannel channel)
    {
        return DiscordChannel.builder()
                .id(DiscordId.from(channel.getIdLong()))
                .guildId(DiscordId.from(channel.getGuild().getIdLong()))
                .name(channel.getName())
                .build();
    }

    public Optional<TextChannel> getChannelFromGuild(long guildId, long channelId)
    {
        Optional<Guild> guild = discordGuildFactory.makeInternalFromId(guildId);
        return guild.map(value -> value.getTextChannelById(channelId));
    }

    @Override
    public Optional<TextChannel> makeInternalFromId(long id)
    {
        return Optional.ofNullable(client.getChannelById(TextChannel.class, id));
    }
}
