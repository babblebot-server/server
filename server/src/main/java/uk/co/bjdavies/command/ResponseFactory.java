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

package uk.co.bjdavies.command;

import discord4j.core.spec.EmbedCreateSpec;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.command.IResponse;

import java.util.function.Consumer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
public class ResponseFactory {

    public static IResponse createResponse(String s, Consumer<EmbedCreateSpec> spec) {
        return new IResponse() {
            @Override
            public String getStringResponse() {
                return s;
            }

            @Override
            public Consumer<EmbedCreateSpec> getEmbedCreateSpecResponse() {
                return spec;
            }

            @Override
            public boolean isStringResponse() {

                if (s == null) {
                    log.error("Response is null, please fix!!!");
                    return false;
                }

                return !s.equals("");
            }
        };
    }

    public static IResponse createStringResponse(String s) {
        return createResponse(s, null);
    }

    public static IResponse createEmbedResponse(Consumer<EmbedCreateSpec> s) {
        return createResponse("", s);
    }

}
