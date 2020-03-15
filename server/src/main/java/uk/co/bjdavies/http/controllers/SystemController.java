package uk.co.bjdavies.http.controllers;

import com.google.inject.Inject;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import uk.co.bjdavies.api.http.Controller;
import uk.co.bjdavies.api.http.Get;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Controller
public class SystemController {

    private final SystemService service;

    @Inject
    public SystemController(SystemService service) {
        this.service = service;
    }

    @Get
    public Publisher<Void> events(HttpServerRequest req, HttpServerResponse res) {
        return service.eventsSse(res);
    }

}
