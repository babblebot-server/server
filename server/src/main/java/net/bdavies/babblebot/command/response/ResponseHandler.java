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

package net.bdavies.babblebot.command.response;

import net.bdavies.babblebot.api.command.IResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public abstract class ResponseHandler
{

    private final Sinks.Many<IResponse> processor;
    private final Type type;


    protected ResponseHandler(Type type, Sinks.Many<IResponse> processor)
    {
        this.processor = processor;
        this.type = type;

    }

    public void handle(Object o)
    {
        if (isAFlux())
        {
            //noinspection unchecked
            handleFlux(((Flux<Object>) o).map(this::getResponse));
        } else if (isAMono())
        {
            //noinspection unchecked
            handleMono(((Mono<Object>) o).map(this::getResponse));
        } else
        {
            handleBase(getResponse(o));
        }
    }

    protected abstract <T> IResponse getResponse(T o);

    private boolean isParamType()
    {
        return type instanceof ParameterizedType;
    }

    private boolean isAFlux()
    {
        if (isParamType())
        {
            return ((ParameterizedType) type).getRawType() == Flux.class;
        }
        return false;
    }

    private boolean isAMono()
    {
        if (isParamType())
        {
            return ((ParameterizedType) type).getRawType() == Mono.class;
        }
        return false;
    }

    private void handleFlux(Flux<IResponse> responses)
    {
        responses.subscribe(processor::tryEmitNext, null, processor::tryEmitComplete);
    }

    private void handleMono(Mono<IResponse> response)
    {
        response.subscribe(processor::tryEmitNext, null, processor::tryEmitComplete);
    }

    private void handleBase(IResponse response)
    {
        processor.tryEmitNext(response);
        processor.tryEmitComplete();
    }
}