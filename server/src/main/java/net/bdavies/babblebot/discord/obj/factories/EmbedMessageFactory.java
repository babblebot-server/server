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

package net.bdavies.babblebot.discord.obj.factories;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.obj.DiscordColor;
import net.bdavies.babblebot.api.obj.message.discord.embed.EmbedAuthor;
import net.bdavies.babblebot.api.obj.message.discord.embed.EmbedFooter;
import net.bdavies.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.bdavies.babblebot.discord.DiscordFacade;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Factory to Create EmbedCreateSpec from Babblebot Object
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.19
 */
@Slf4j
public class EmbedMessageFactory
{
    public static EmbedCreateSpec fromBabblebot(EmbedMessage message)
    {
        EmbedCreateSpec embedCreateSpec = EmbedCreateSpec.builder().build();
        if (message.getTitle() != null)
        {
            embedCreateSpec = embedCreateSpec.withTitle(message.getTitle());
        }

        if (message.getDescription() != null)
        {
            embedCreateSpec = embedCreateSpec.withDescription(message.getDescription());
        }

        if (message.getUrl() != null)
        {
            embedCreateSpec = embedCreateSpec.withUrl(message.getUrl());
        }

        if (message.getTimestamp() != null)
        {
            embedCreateSpec = embedCreateSpec.withTimestamp(message.getTimestamp());
        }

        if (message.getColor() != null)
        {
            embedCreateSpec = embedCreateSpec.withColor(Color.of(message.getColor().getRGB()));
        }

        if (message.getFooter() != null)
        {
            val footer = message.getFooter();
            embedCreateSpec = embedCreateSpec.withFooter(
                    EmbedCreateFields.Footer.of(footer.getText(), footer.getIconUrl()));
        }

        if (message.getAuthor() != null)
        {
            val author = message.getAuthor();
            embedCreateSpec = embedCreateSpec.withAuthor(
                    EmbedCreateFields.Author.of(author.getName(), author.getUrl(),
                            author.getIconUrl()));
        }

        if (message.getImage() != null)
        {
            embedCreateSpec = embedCreateSpec.withImage(message.getImage());
        }

        if (message.getThumbnail() != null)
        {
            embedCreateSpec = embedCreateSpec.withThumbnail(message.getThumbnail());
        }

        embedCreateSpec = embedCreateSpec.withFields(message.getFields().stream().map(f -> EmbedCreateFields
                .Field.of(f.getName(), f.getValue(), f.isInline())).collect(Collectors.toList()));

        return embedCreateSpec;
    }

    public static void addDefaults(EmbedMessage em, IApplication application, Mono<Guild> guild)
    {
        if (em.getFooter() == null)
        {
            em.setFooter(EmbedFooter.builder()
                    .text("Server Version: " + application.getServerVersion()).build());
        }
        if (em.getAuthor() == null)
        {
            em.setAuthor(EmbedAuthor.builder().name("BabbleBot")
                    .url("https://github.com/bendavies99/BabbleBot-Server").build());
        }
        if (em.getTimestamp() == null)
        {
            em.setTimestamp(Instant.now());
        }
        Optional<Color> color = guild.flatMap(
                        g -> application.get(DiscordFacade.class).getClient().getSelf()
                                .flatMap(u -> u.asMember(g.getId()).flatMap(PartialMember::getColor)))
                .blockOptional();
        if (em.getColor() == null && color.isPresent())
        {
            em.setColor(DiscordColor.from(color.get().getRGB()));
        }
    }
}
