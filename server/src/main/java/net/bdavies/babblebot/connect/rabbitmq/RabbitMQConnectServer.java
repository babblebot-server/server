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
import net.bdavies.babblebot.connect.ConnectConfigurationProperties;
import net.bdavies.babblebot.connect.ConnectServer;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * RabbitMQServer implementation of the server
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.13
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "connect.rabbitmq", name = "enabled", havingValue = "true")
public class RabbitMQConnectServer implements ConnectServer
{
    private final ConnectionFactory connectionFactory;

    public RabbitMQConnectServer(ConnectConfigurationProperties connectConfig)
    {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(connectConfig.getRabbitmq().getHost());
        connectionFactory.setPort(connectConfig.getRabbitmq().getPort());
    }

    @Override
    @SneakyThrows
    public void sendMessage(IConnectQueue<?> connectQueue, Serializable serializable)
    {
        try (Connection connection = connectionFactory.newConnection())
        {
            try (Channel channel = connection.createChannel())
            {
                if (connectQueue.isMulticast())
                {
                    channel.exchangeDeclare(connectQueue.getQueueName(), BuiltinExchangeType.FANOUT);
                } else
                {
                    channel.queueDeclare(connectQueue.getQueueName(), true, false, false, null);
                }
                byte[] msg = SerializationUtils.serialize(serializable);
                if (connectQueue.isMulticast())
                {
                    channel.basicPublish(connectQueue.getQueueName(), "", null, msg);
                } else
                {
                    channel.basicPublish("", connectQueue.getQueueName(),
                            MessageProperties.MINIMAL_PERSISTENT_BASIC, msg);
                }
            }
        }
    }
}
