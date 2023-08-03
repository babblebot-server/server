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

package net.babblebot.discord;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.config.IDiscordConfig;
import net.babblebot.discord.listener.*;
import net.babblebot.exception.DiscordTokenNotPresentException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * This class will DiscordAPI Client by making a {@link JDA}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordLoginService
{
    private final IDiscordConfig config;
    private final ReadyListener readyListener;
    private final MessageReceivedListener messageReceivedListener;
    private final SlashCommandListener slashCommandListener;
    private final DropdownListener dropdownListener;
    private final ButtonListener buttonListener;

    @Getter
    private JDA client;


    @PostConstruct
    private void setupClient()
    {
        try
        {
            if (config.getToken().equals(""))
            {
                throw new DiscordTokenNotPresentException();
            }
            log.info("Setting up Discord Client");
            client = JDABuilder
                    .createDefault(config.getToken())
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(
                            readyListener,
                            messageReceivedListener,
                            slashCommandListener,
                            dropdownListener,
                            buttonListener
                    )
                    .build();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @PreDestroy
    void onShutdown()
    {
        this.client.shutdownNow();
    }

    @Bean
    JDA internalDiscordClient()
    {
        return this.client;
    }
}
