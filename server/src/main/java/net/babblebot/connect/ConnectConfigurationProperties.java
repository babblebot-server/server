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

package net.babblebot.connect;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.connect.rabbitmq.RabbitMQConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Properties for Babblebot Connect
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.26
 */
@Slf4j
@ConfigurationProperties(prefix = "connect")
@Configuration
@Data
public class ConnectConfigurationProperties
{
    private boolean useConnect;
    private boolean leader = true;
    private boolean worker;
    private RabbitMQConfig rabbitmq;

    @PostConstruct
    void init()
    {
        if (worker)
        {
            setLeader(false);
        }
        if (leader)
        {
            setWorker(false);
        }
    }
}
