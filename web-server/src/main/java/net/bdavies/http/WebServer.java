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

package net.bdavies.http;

import com.google.inject.Inject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.config.IHttpConfig;
import net.bdavies.api.discord.IDiscordFacade;
import net.bdavies.http.controllers.SystemController;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

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
