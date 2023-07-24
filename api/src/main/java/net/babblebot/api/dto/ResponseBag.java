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

package net.babblebot.api.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.util.FunctionEx;

import java.util.UUID;

/**
 * Generic Response for a Rest Request
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.27
 */
@Slf4j
@Data
@SuperBuilder
@Jacksonized
public class ResponseBag
{
    private final String uniqueId;
    private final long exchangeTime;
    private final int status;
    private final String message;
    @JsonUnwrapped
    private final Response body;

    public static ResponseBag from(FunctionEx<String, Response> func)
    {
        String uniqueId = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        Response resp;
        try
        {
            resp = func.apply(uniqueId);
        }
        catch (Exception e)
        {
            resp = Response.from("Something went wrong, please check the logs Request-ID: [" + uniqueId + "]",
                    500);
            log.info("Something went wrong with request [{}]", uniqueId, e);
        }
        long end = System.currentTimeMillis();

        return ResponseBag.builder()
                .exchangeTime(end - start)
                .message(resp.getMessage())
                .uniqueId(uniqueId)
                .body(resp)
                .status(resp.getStatus())
                .build();
    }
}
