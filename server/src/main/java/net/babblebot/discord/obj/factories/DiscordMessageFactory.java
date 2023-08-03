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
import net.babblebot.api.obj.message.discord.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Factory for creating Discord Messages
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.28
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DiscordMessageFactory
{
    private final DiscordGuildFactory discordGuildFactory;
    private final DiscordChannelFactory discordChannelFactory;
    private final DiscordUserFactory discordUserFactory;
    private final JDA client;

    public DiscordMessage makeFromInternal(Guild guild, TextChannel channel, User author,
                                           long messageId, String messageContent)
    {
        DiscordGuild discordGuild = discordGuildFactory.makeFromInternal(guild);
        DiscordChannel discordChannel = discordChannelFactory.makeFromInternal(channel);
        DiscordUser discordUser = discordUserFactory.makeFromInternal(author);

        return DiscordMessage.builder()
                .id(DiscordId.from(messageId))
                .author(discordUser)
                .guild(discordGuild)
                .channel(discordChannel)
                .content(messageContent)
                .build();
    }

    public DiscordMessage makeFromInternal(Message message)
    {
        return makeFromInternal(message.getGuild(), message.getChannel().asTextChannel(), message.getAuthor(),
                message.getIdLong(), message.getContentRaw());
    }

    public Optional<DiscordMessage> makeFromIds(long guildId,
                                                long channelId, long authorId,
                                                long messageId, String messageContent)
    {
        Optional<Guild> guild = discordGuildFactory.makeInternalFromId(guildId);
        if (guild.isPresent())
        {
            Optional<TextChannel> channel = discordChannelFactory.makeInternalFromId(channelId);
            if (channel.isPresent())
            {
                Optional<User> author = discordUserFactory.makeInternalFromId(authorId);
                if (author.isPresent())
                {
                    return Optional.of(makeFromInternal(guild.get(), channel.get(),
                            author.get(), messageId, messageContent));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Message> makeInternalFromDiscordMessage(DiscordMessage message)
    {
        return makeInternalFromIds(
                message.getGuild().getId().toLong(),
                message.getChannel().getId().toLong(),
                message.getId().toLong()
        );
    }

    public Optional<Message> makeInternalFromIds(long guildId, long channelId, long messageId)
    {
        Optional<Guild> guild = discordGuildFactory.makeInternalFromId(guildId);
        if (guild.isPresent())
        {
            Optional<TextChannel> channel = Optional.ofNullable(guild.get().getTextChannelById(channelId));
            if (channel.isPresent())
            {
                return Optional.ofNullable(channel.get().retrieveMessageById(messageId).complete());
            }
        }

        return Optional.empty();
    }
}
