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

package net.bdavies.babblebot.command.renderer;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.InteractionResponseData;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.InteractionResponseType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.command.IResponse;
import net.bdavies.babblebot.api.obj.message.discord.DiscordMessage;
import net.bdavies.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.bdavies.babblebot.command.ResponseFactory;
import net.bdavies.babblebot.command.errors.UsageException;
import net.bdavies.babblebot.discord.obj.factories.EmbedMessageFactory;
import reactor.core.publisher.Mono;

/**
 * Renderer for an Interaction event from Discord
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.13
 */
@Slf4j
@RequiredArgsConstructor
public class DiscordInteractionEventRenderer implements CommandRenderer
{
    private final GatewayDiscordClient client;
    private final DiscordMessage message;
    private final TextChannel channel;
    private final IApplication application;

    @Override
    public void render(IResponse response)
    {
        if (response.isStringResponse())
        {
            reply(InteractionApplicationCommandCallbackSpec.create()
                    .withContent(response.getStringResponse())).subscribe();
        } else
        {
            EmbedMessage em = response.getEmbedCreateSpecResponse().get();
            EmbedMessageFactory.addDefaults(em, application, channel.getGuild());
            EmbedCreateSpec spec = EmbedMessageFactory.fromBabblebot(em);
            reply(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(spec)
                    .build()).subscribe();
        }
    }

    private Mono<Void> reply(InteractionApplicationCommandCallbackSpec spec)
    {
        val data = spec.asRequest();
        InteractionResponseType type = InteractionResponseType.CHANNEL_MESSAGE_WITH_SOURCE;
        InteractionResponseData responseData = InteractionResponseData.builder()
                .type(type.getValue())
                .data(Possible.of(data.getJsonPayload()))
                .build();

        return client.getRestClient().getInteractionService()
                .createInteractionResponse(message.getId().toLong(), message.getToken(),
                        data.withRequest(responseData));
    }

    @Override
    public boolean onError(Exception e)
    {
        if (e instanceof UsageException)
        {
            render(ResponseFactory.createStringResponse(e.getLocalizedMessage()));
            return true;
        }
        return false;
    }
}
