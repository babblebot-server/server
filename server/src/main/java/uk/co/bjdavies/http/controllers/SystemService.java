package uk.co.bjdavies.http.controllers;

import com.google.inject.Inject;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerResponse;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.discord.IDiscordFacade;
import uk.co.bjdavies.http.WebServer;

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
