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

package net.babblebot.events;

import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.IApplication;
import net.babblebot.api.events.IEvent;
import net.babblebot.api.events.IEventDispatcher;
import net.babblebot.connect.ConnectConfigurationProperties;
import net.babblebot.connect.queue.EventDispatcherQueue;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.function.Consumer;

/**
 * Event Dispatcher Implementation for Babblebot
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.26
 */
@Slf4j
@Component
public class EventDispatcher implements IEventDispatcher
{
    private final Map<Class<? extends IEvent>, Sinks.Many<IEvent>> publishers = new HashMap<>();
    private final Map<Class<? extends IEvent>, List<Consumer<IEvent>>> priorityPublishers = new HashMap<>();
    private final List<String> sentPackages = new ArrayList<>();
    private final ConnectConfigurationProperties connectConfig;
    private final IApplication application;

    public EventDispatcher(ConnectConfigurationProperties connectConfig, IApplication application)
    {
        this.connectConfig = connectConfig;
        this.application = application;
        if (connectConfig.isUseConnect())
        {
            EventDispatcherQueue queue = application.get(EventDispatcherQueue.class);
            queue.setMessageHandler(e -> dispatchInternal(e, false));
        }
    }

    @Override
    public <T extends IEvent> Flux<T> on(Class<T> clazz)
    {
        return onInternal(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends IEvent> Flux<T> onInternal(Class<T> clazz)
    {
        publishers.putIfAbsent(clazz, Sinks.many().multicast().onBackpressureBuffer());
        return (Flux<T>) publishers.get(clazz).asFlux();
    }

    /**
     * This should not be used by a plugin as it allows babblebot to do some priority
     * tasks before giving the event to the consumer
     *
     * @param clazz    The event class
     * @param consumer the consumer
     * @param <T>      the type of event
     */
    public <T extends IEvent> void onPriority(Class<T> clazz, Consumer<T> consumer)
    {
        if (!consumer.getClass().getPackageName().startsWith("net.babblebot"))
        {
            log.error("You cannot set a priority event outside main babblebot");
            return;
        }
        priorityPublishers.putIfAbsent(clazz, new LinkedList<>());
        //noinspection unchecked
        Consumer<IEvent> eventConsumer = e -> consumer.accept((T) e);
        priorityPublishers.get(clazz).add(eventConsumer);
    }

    @Override
    public <T extends IEvent> void dispatch(T obj)
    {
        dispatchInternal(obj, true);
    }


    public <T extends IEvent> void dispatchInternal(T obj, boolean send)
    {
        dispatchInternal(obj, send, false);
    }

    public <T extends IEvent> void dispatchInternal(T obj, boolean send, boolean connectOnly)
    {
        if (sentPackages.stream().noneMatch(p -> p.equals(obj.getUniqueId())))
        {
            log.info("Dispatching event: {}", obj);
            publishers.putIfAbsent(obj.getClass(), Sinks.many().multicast().onBackpressureBuffer());
            priorityPublishers.putIfAbsent(obj.getClass(), new LinkedList<>());
            if (send && connectConfig.isUseConnect())
            {
                sentPackages.add(obj.getUniqueId());
                EventDispatcherQueue queue = application.get(EventDispatcherQueue.class);
                queue.send(obj);
            }
            if (!connectOnly)
            {
                priorityPublishers.get(obj.getClass()).forEach(c -> c.accept(obj));
                publishers.get(obj.getClass()).emitNext(obj, Sinks.EmitFailureHandler.FAIL_FAST);
            }
        } else
        {
            sentPackages.remove(obj.getUniqueId());
        }
    }
}
