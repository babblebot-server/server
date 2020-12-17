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

package uk.co.bjdavies.command.response;

import discord4j.core.spec.EmbedCreateSpec;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;
import net.bdavies.api.command.IResponse;
import uk.co.bjdavies.command.response.handlers.EmbedHandler;
import uk.co.bjdavies.command.response.handlers.StringHandler;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
public class ResponseHandlerFactory {

    @Nullable
    public static ResponseHandler getHandler(Type t, FluxProcessor<IResponse, IResponse> processor) {

        if (t instanceof ParameterizedType) {

            ParameterizedType pType = (ParameterizedType) t;
            if (isAMono(pType.getRawType()) || isAFlux(pType.getRawType())) {
                Type type = pType.getActualTypeArguments()[0];
                return getBaseHandler(t, type, processor);
            } else if (pType.getRawType().equals(Consumer.class)) {
                return getBaseHandler(t, t, processor);
            }
            return null;
        } else {
            return getBaseHandler(t, t, processor);
        }
    }


    @Nullable
    private static ResponseHandler getBaseHandler(Type raw, Type base, FluxProcessor<IResponse, IResponse> processor) {
        log.info("Handling a type of: " + base);
        if (base.equals(String.class)) {
            return new StringHandler(raw, processor);
        } else if (base instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) base;
            Type type = pType.getActualTypeArguments()[0];
            if (type.equals(EmbedCreateSpec.class)) {
                return new EmbedHandler(raw, processor);
            }
        }

        return null;
    }

    private static boolean isAFlux(Type o) {
        return o == Flux.class;
    }

    private static boolean isAMono(Type o) {
        return o == Mono.class;
    }

}
