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
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.FollowupMessageRequest;
import discord4j.rest.util.MultipartRequest;
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
 * @since 3.0.0-rc.20
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
            reply(InteractionFollowupCreateSpec.create()
                    .withContent(response.getStringResponse())).subscribe();
        } else
        {
            EmbedMessage em = response.getEmbedCreateSpecResponse().get();
            EmbedMessageFactory.addDefaults(em, application, channel.getGuild());
            EmbedCreateSpec spec = EmbedMessageFactory.fromBabblebot(em);
            reply(InteractionFollowupCreateSpec.builder()
                    .addEmbed(spec)
                    .build()).subscribe();
        }
    }

    private Mono<Void> reply(InteractionFollowupCreateSpec spec)
    {
        val data = spec.asRequest();
        FollowupMessageRequest newBody = FollowupMessageRequest.builder()
                .from(data.getJsonPayload())
                .build();
        val appInfo = client.getApplicationInfo().blockOptional().orElseThrow();
        return client.getRestClient().getWebhookService()
                .executeWebhook(appInfo.getId().asLong(), message.getToken(), true,
                        MultipartRequest.ofRequest(newBody))
                .flatMap(m -> Mono.empty());
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
