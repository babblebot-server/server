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

package net.bdavies.babblebot.api.connect;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Connect Queue Interface
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.22
 */
public interface IConnectQueue<T extends Serializable>
{
    /**
     * Get Connect queue name that will be used with the messaging service
     *
     * @return String
     */
    default String getQueueName()
    {
        return "babblebot-connect-message-queue-" + this.getClass().getSimpleName();
    }

    default boolean isMulticast()
    {
        return false;
    }

    /**
     * Determines whether the queue will be listened to by a leader or not
     *
     * @return boolean
     */
    default boolean isWorkerOnly()
    {
        return false;
    }

    /**
     * Send an object to a worker that will complete the task
     *
     * @param obj the object
     */
    void send(T obj);

    /**
     * Set the message handler for a worker so the worker can listen to messages on the queue
     *
     * @param obj the consumer for all the messages on the queue
     */
    void setMessageHandler(Consumer<T> obj);
}