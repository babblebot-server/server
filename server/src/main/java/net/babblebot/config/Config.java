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

package net.babblebot.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.config.IConfig;
import net.babblebot.api.config.IDiscordConfig;
import net.babblebot.api.config.ISystemConfig;

/**
 * BabbleBot, open-source Discord Bot
 * Author: Ben Davies
 * Class Name: Config.java
 * Compiled Class Name: Config.class
 * Date Created: 30/01/2018
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder(toBuilder = true)
@Jacksonized
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Config implements IConfig
{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    /**
     * This is the config settings for the discord part of this bot.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @Builder.Default
    private DiscordConfig discord = DiscordConfig.builder().build();


    /**
     * This is the config settings for the system part of this bot.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @Builder.Default
    private SystemConfig system = SystemConfig.builder().build();


    @Override
    public IDiscordConfig getDiscordConfig()
    {
        return discord;
    }

    @Override
    public ISystemConfig getSystemConfig()
    {
        return system;
    }
}
