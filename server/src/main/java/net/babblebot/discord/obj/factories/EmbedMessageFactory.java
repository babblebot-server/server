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

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.IApplication;
import net.babblebot.api.obj.DiscordColor;
import net.babblebot.api.obj.message.discord.embed.EmbedAuthor;
import net.babblebot.api.obj.message.discord.embed.EmbedFooter;
import net.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.babblebot.api.service.IVersionService;
import net.babblebot.discord.DiscordFacade;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Instant;
import java.util.Optional;

/**
 * Factory to Create EmbedCreateSpec from Babblebot Object
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.29
 */
@Slf4j
public class EmbedMessageFactory
{
    public static MessageEmbed fromBabblebot(EmbedMessage message)
    {
        EmbedBuilder builder = new EmbedBuilder();
        if (message.getTitle() != null)
        {
            builder.setTitle(message.getTitle());
        }

        if (message.getDescription() != null)
        {
            builder.setDescription(message.getDescription());
        }

        if (message.getUrl() != null)
        {
            builder.setUrl(message.getUrl());
        }

        if (message.getTimestamp() != null)
        {
            builder.setTimestamp(message.getTimestamp());
        }

        if (message.getColor() != null)
        {
            builder.setColor(message.getColor().getRGB());
        }

        if (message.getFooter() != null)
        {
            val footer = message.getFooter();
            builder.setFooter(footer.getText(), footer.getIconUrl());
        }

        if (message.getAuthor() != null)
        {
            val author = message.getAuthor();
            builder.setAuthor(author.getName(), author.getUrl(), author.getIconUrl());
        }

        if (message.getImage() != null)
        {
            builder.setImage(message.getImage());
        }

        if (message.getThumbnail() != null)
        {
            builder.setThumbnail(message.getThumbnail());
        }

        message.getFields().forEach(field -> {
            builder.addField(field.getName(), field.getValue(), field.isInline());
        });

        return builder.build();
    }

    public static void addDefaults(EmbedMessage em, IApplication application, Guild guild)
    {
        if (em.getFooter() == null)
        {
            em.setFooter(EmbedFooter.builder()
                    .text("Server Version: " + application.get(IVersionService.class).getVersionStr())
                    .build());
        }
        if (em.getAuthor() == null)
        {
            em.setAuthor(EmbedAuthor.builder()
                    .name("BabbleBot")
                    .url("https://github.com/babblebot-server/server")
                    .iconUrl("https://avatars.githubusercontent.com/u/138989349?s=256&v=4")
                    .build());
        }
        if (em.getTimestamp() == null)
        {
            em.setTimestamp(Instant.now());
        }

        DiscordFacade facade = application.get(DiscordFacade.class);
        Optional<Member> member = Optional
                .ofNullable(guild.getMemberById(facade.getBotUser().getId().toLong()));

        if (member.isPresent()) {
            Member m = member.get();
            if (em.getColor() == null) {
                em.setColor(DiscordColor.from(m.getColorRaw()));
            }
        }
    }
}
