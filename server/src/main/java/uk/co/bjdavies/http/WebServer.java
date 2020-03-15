package uk.co.bjdavies.http;

import com.google.inject.Inject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.config.IHttpConfig;
import uk.co.bjdavies.api.discord.IDiscordFacade;
import uk.co.bjdavies.api.http.Get;
import uk.co.bjdavies.http.controllers.SystemController;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

/**
 * This is where the HTTP Server lies.
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class WebServer {

    private final IApplication application;

    private final IHttpConfig config;

    private final IDiscordFacade facade;

    private final HttpServer httpServer;
    private DisposableServer dServer;

    @SneakyThrows
    @Inject
    public WebServer(IApplication application, IHttpConfig config, IDiscordFacade facade) {
        this.application = application;
        this.config = config;
        this.facade = facade;
        httpServer = HttpServer.create()
                .compress(true)
                .wiretap(true)
                .port(config.getPort())
                .handle(this::handle);
    }

    public static ByteBuf toByteBuf(String data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write("event: test\n".getBytes(Charset.defaultCharset()));
            out.write(("data: " + data).getBytes(Charset.defaultCharset()));
            out.write("\n\n".getBytes(Charset.defaultCharset()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ByteBufAllocator.DEFAULT
                .buffer()
                .writeBytes(out.toByteArray());
    }

    private Publisher<Void> handle(HttpServerRequest in, HttpServerResponse out) {
        SystemController controller = application.get(SystemController.class);
        Flux<Method> methods = Flux.fromArray(SystemController.class.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Get.class));
        String uri = in.uri().replace("/", "");

        return methods.filter(e -> e.getReturnType().equals(Publisher.class)).filter(m -> m.getName().equals(uri))
                .flatMap(m -> invokeMethod(m, controller, in, out));

    }

    private Publisher<? extends Void> invokeMethod(Method m, SystemController controller, HttpServerRequest in, HttpServerResponse out) {
        try {
            //noinspection unchecked
            return (Publisher<? extends Void>) m.invoke(controller, in, out);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return Mono.empty();
        }
    }

    public void start() {
        log.info("Starting Web Server");
        dServer = httpServer.bindNow();
    }

    public void stop() {
        log.info("Shutting Down Web Server");
        dServer.disposeNow();
    }


}
