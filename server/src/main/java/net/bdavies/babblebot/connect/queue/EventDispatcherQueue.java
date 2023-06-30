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

package net.bdavies.babblebot.connect.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.connect.ConnectQueue;
import net.bdavies.babblebot.api.connect.IConnectQueue;
import net.bdavies.babblebot.api.events.IEvent;
import net.bdavies.babblebot.connect.ConnectClient;
import net.bdavies.babblebot.connect.ConnectServer;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Event Dispatcher Queue
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.20
 */
@Slf4j
@ConnectQueue
@RequiredArgsConstructor
public class EventDispatcherQueue implements IConnectQueue<IEvent>
{
    private final IApplication application;

    @Override
    public void send(IEvent obj)
    {
        application.get(ConnectServer.class)
                .sendMessage(this, obj);
    }

    @Override
    public void setMessageHandler(Consumer<IEvent> obj)
    {
        Consumer<Serializable> s = ser -> obj.accept((IEvent) ser);
        application.get(ConnectClient.class).registerMessageHandler(this, s);
    }

    @Override
    public boolean isMulticast()
    {
        return true;
    }
}
