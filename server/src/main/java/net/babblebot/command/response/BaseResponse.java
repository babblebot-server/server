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

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.command.IResponse;
import net.babblebot.api.obj.message.discord.TTSMessage;
import net.babblebot.api.obj.message.discord.embed.EmbedMessage;

import java.util.function.Supplier;

/**
 * Base Response Class
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.24
 */
@Slf4j
@Data
@Builder(toBuilder = true)
public class BaseResponse implements IResponse
{
    private final String stringResponse;
    private final Supplier<EmbedMessage> embedMessageResponse;
    private final TTSMessage ttsMessage;

    @Override
    public String getStringResponse()
    {
        return stringResponse;
    }

    @Override
    public TTSMessage getTTSMessage()
    {
        return ttsMessage;
    }

    @Override
    public Supplier<EmbedMessage> getEmbedCreateSpecResponse()
    {
        return embedMessageResponse;
    }

    @Override
    public boolean isStringResponse()
    {
        return stringResponse != null && !"".equals(stringResponse);
    }

    @Override
    public boolean isTTSResponse()
    {
        return ttsMessage != null;
    }
}
