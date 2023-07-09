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

package net.babblebot.command.renderer;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.FollowupMessageRequest;
import discord4j.rest.util.MultipartRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.IApplication;
import net.babblebot.api.command.IResponse;
import net.babblebot.api.discord.DiscordMessageSendSpec;
import net.babblebot.api.discord.IDiscordMessagingService;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.command.ResponseFactory;
import net.babblebot.command.errors.UsageException;
import reactor.core.publisher.Mono;

/**
 * Renderer for an Interaction event from Discord
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.23
 */
@Slf4j
@RequiredArgsConstructor
public class DiscordInteractionEventRenderer implements CommandRenderer
{
    private final GatewayDiscordClient client;
    private final DiscordMessage message;
    private final IApplication application;
    private final IDiscordMessagingService service;
    private boolean replied;

    @Override
    public void render(IResponse response)
    {
        if (response.isStringResponse())
        {
            reply(InteractionFollowupCreateSpec.builder()
                    .content("--------------------")
                    .build()).subscribe();

            service.send(message.getGuild(), message.getChannel(),
                            DiscordMessageSendSpec.fromString(response.getStringResponse()))
                    .subscribe();
        } else if (response.isTTSResponse())
        {
            reply(InteractionFollowupCreateSpec.builder()
                    .content("--------------------")
                    .build()).subscribe();
            service.send(message.getGuild(), message.getChannel(),
                            DiscordMessageSendSpec.fromTts(response.getTTSMessage().getContent()))
                    .subscribe();
        } else
        {
            reply(InteractionFollowupCreateSpec.builder()
                    .content("--------------------")
                    .build()).subscribe();
            service.send(message.getGuild(), message.getChannel(),
                            DiscordMessageSendSpec.fromEmbed(response.getEmbedCreateSpecResponse().get()))
                    .subscribe();
        }
    }

    private Mono<Void> reply(InteractionFollowupCreateSpec spec)
    {
        if (replied)
        {
            return Mono.empty();
        }
        val data = spec.asRequest();
        FollowupMessageRequest newBody = FollowupMessageRequest.builder()
                .from(data.getJsonPayload())
                .build();
        return client.getApplicationInfo()
                .flatMap(appInfo -> client.getRestClient().getWebhookService()
                        .executeWebhook(appInfo.getId().asLong(), message.getToken(), true,
                                MultipartRequest.ofRequest(newBody))
                        .onErrorComplete()
                        .doOnNext(d -> replied = true)
                        .flatMap(m -> Mono.empty()));
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
