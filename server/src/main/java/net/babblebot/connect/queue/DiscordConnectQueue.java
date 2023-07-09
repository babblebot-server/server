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

package net.babblebot.connect.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.IApplication;
import net.babblebot.api.connect.ConnectQueue;
import net.babblebot.api.connect.IConnectQueue;
import net.babblebot.connect.ConnectClient;
import net.babblebot.connect.ConnectServer;
import net.babblebot.connect.DiscordConnectMessage;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Discord Connect Queue
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.24
 */
@ConnectQueue
@Slf4j
@RequiredArgsConstructor
public class DiscordConnectQueue implements IConnectQueue<DiscordConnectMessage>
{
    private final IApplication application;

    @Override
    public void send(DiscordConnectMessage obj)
    {
        ConnectServer connectServer = application.get(ConnectServer.class);
        connectServer.sendMessage(this, obj);
    }

    @Override
    public void setMessageHandler(Consumer<DiscordConnectMessage> obj)
    {
        ConnectClient connectClient = application.get(ConnectClient.class);
        Consumer<Serializable> consumer = s -> obj.accept((DiscordConnectMessage) s);
        connectClient.registerMessageHandler(this, consumer);
    }

    @Override
    public boolean isWorkerOnly()
    {
        return true;
    }
}
