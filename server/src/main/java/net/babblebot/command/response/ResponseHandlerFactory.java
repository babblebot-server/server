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

package net.babblebot.command.response;

import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.command.IResponse;
import net.babblebot.api.discord.DiscordMessageSendSpec;
import net.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.babblebot.api.obj.message.discord.interactions.button.Button;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownMenu;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownView;
import net.babblebot.command.response.handlers.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
public class ResponseHandlerFactory
{

    public static ResponseHandler getHandler(Type t, Sinks.Many<IResponse> processor)
    {

        if (t instanceof ParameterizedType)
        {

            ParameterizedType pType = (ParameterizedType) t;
            if (isAMono(pType.getRawType()) || isAFlux(pType.getRawType()))
            {
                Type type = pType.getActualTypeArguments()[0];
                return getBaseHandler(t, type, processor);
            } else if (pType.getRawType().equals(Consumer.class))
            {
                return getBaseHandler(t, t, processor);
            }
            return null;
        } else
        {
            return getBaseHandler(t, t, processor);
        }
    }


    private static ResponseHandler getBaseHandler(Type raw, Type base,
                                                  Sinks.Many<IResponse> processor)
    {
        log.info("Handling a type of: " + base);
        if (base.equals(String.class))
        {
            return new StringHandler(raw, processor);
        } else if (base instanceof ParameterizedType)
        {
            ParameterizedType pType = (ParameterizedType) base;
            Type type = pType.getActualTypeArguments()[0];
            if (type.equals(EmbedMessage.class))
            {
                return new EmbedHandler(raw, processor);
            }
        } else if (base.equals(EmbedMessage.class))
        {
            return new EmbedHandler(raw, processor);
        } else if (base.equals(DiscordMessageSendSpec.class))
        {
            return new SendMessageSpecHandler(raw, processor);
        } else if (base.equals(DropdownView.class))
        {
            return new DropdownViewHandler(raw, processor);
        } else if (base.equals(DropdownMenu.class))
        {
            return new DropdownMenuHandler(raw, processor);
        } else if (base.equals(Button.class))
        {
            return new ButtonHandler(raw, processor);
        }

        return null;
    }

    private static boolean isAFlux(Type o)
    {
        return o == Flux.class;
    }

    private static boolean isAMono(Type o)
    {
        return o == Mono.class;
    }

}
