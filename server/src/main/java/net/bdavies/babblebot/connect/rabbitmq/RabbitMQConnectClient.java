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

package net.bdavies.babblebot.connect.rabbitmq;

import com.rabbitmq.client.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.babblebot.api.connect.IConnectQueue;
import net.bdavies.babblebot.connect.ConnectClient;
import net.bdavies.babblebot.connect.ConnectConfigurationProperties;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * RabbitMQServer implementation of the server
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.18
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "connect.rabbitmq", name = "enabled", havingValue = "true")
public class RabbitMQConnectClient implements ConnectClient
{
    private final ConnectionFactory connectionFactory;
    private final ConnectConfigurationProperties connectConfig;
    private final Map<IConnectQueue<?>, Consumer<? extends Serializable>> handlers = new HashMap<>();

    @SneakyThrows
    public RabbitMQConnectClient(ConnectConfigurationProperties connectConfig)
    {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(connectConfig.getRabbitmq().getHost());
        connectionFactory.setPort(connectConfig.getRabbitmq().getPort());
        this.connectConfig = connectConfig;
    }

    @Override
    public void registerMessageHandler(IConnectQueue<?> connectQueue,
                                       Consumer<Serializable> consumer)
    {
        if (handlers.containsKey(connectQueue))
        {
            handlers.put(connectQueue, consumer);
        } else
        {
            handlers.put(connectQueue, consumer);
            registerQueue(connectQueue);
        }
    }

    @SneakyThrows
    private <T extends Serializable> void registerQueue(IConnectQueue<T> connectQueue)
    {
        if (connectQueue.isWorkerOnly() && connectConfig.isLeader())
        {
            return;
        }
        //noinspection resource
        final Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        String queueName = "";
        if (connectQueue.isMulticast())
        {
            channel.exchangeDeclare(connectQueue.getQueueName(), BuiltinExchangeType.FANOUT);
            queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, connectQueue.getQueueName(), "");
        } else
        {
            channel.queueDeclare(connectQueue.getQueueName(), true, false, false, null);
            channel.basicQos(1);
        }

        DeliverCallback cb = (tag, del) -> {

            T msg;
            try
            {
                msg = SerializationUtils.deserialize(del.getBody());
            }
            catch (SerializationException e)
            {
                log.error("Failed to deserialize message", e);
                channel.basicReject(del.getEnvelope().getDeliveryTag(), false);
                return;
            }
            if (handlers.get(connectQueue) == null)
            {
                channel.basicReject(del.getEnvelope().getDeliveryTag(), true);
            } else
            {
                log.info("Received Connect message: {}", msg);
                //noinspection unchecked
                Consumer<T> consumer = (Consumer<T>) handlers.get(connectQueue);
                consumer.accept(msg);
                channel.basicAck(del.getEnvelope().getDeliveryTag(), false);
            }
        };

        channel.basicConsume(connectQueue.isMulticast() ? queueName : connectQueue.getQueueName(), false, cb,
                tag -> {});
    }
}
