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

package net.bdavies.babblebot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.babblebot.api.config.IConfig;
import net.bdavies.babblebot.api.config.IDiscordConfig;
import net.bdavies.babblebot.api.config.ISystemConfig;
import net.bdavies.babblebot.exception.DiscordTokenNotPresentException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Configuration for the Config Object
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.14
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ConfigConfiguration
{
    private final GenericApplicationContext applicationContext;
    private final DiscordConfigurationProperties discordConfigurationProperties;
    private IConfig config;

    @Bean
    IConfig config()
    {
        if ("".equals(discordConfigurationProperties.getToken()) ||
                discordConfigurationProperties.getToken() == null)
        {
            throw new DiscordTokenNotPresentException();
        }
        if (config == null)
        {
            Config c = (Config) ConfigFactory.makeConfig(
                    applicationContext.getBean(ConfigRepository.class));
            c.setDiscord(c.getDiscord().toBuilder().token(discordConfigurationProperties.getToken()).build());
            config = c;
        }
        return config;
    }

    @Bean
    IDiscordConfig discordConfig()
    {
        return config().getDiscordConfig();
    }

    @Bean
    ISystemConfig systemConfig()
    {
        return config().getSystemConfig();
    }
}
