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

package net.bdavies.http.controllers;

import com.google.inject.Inject;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import io.netty.handler.codec.http.HttpHeaderNames;
import net.bdavies.api.IApplication;
import net.bdavies.api.discord.IDiscordFacade;
import net.bdavies.http.WebServer;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerResponse;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class SystemService {

    private final IDiscordFacade facade;
    private final IApplication application;

    @Inject
    public SystemService(IDiscordFacade facade, IApplication application) {
        this.facade = facade;
        this.application = application;
    }

    public Publisher<Void> eventsSse(HttpServerResponse res) {
        return res
          .header(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
          .sse()
          .send(facade
            .getClient()
            .getEventDispatcher()
            .on(MessageCreateEvent.class)
            .filterWhen(m ->
              m.getMessage()
                .getAuthorAsMember()
                .map(User::isBot))
            .map(m ->
              WebServer.toByteBuf(m.
                getMessage().
                getContent()
              )
            ));
    }
}
