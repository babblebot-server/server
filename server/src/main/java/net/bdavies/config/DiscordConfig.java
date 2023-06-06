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

package net.bdavies.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.config.IDiscordConfig;

/**
 * BabbleBot, open-source Discord Bot
 * Author: Ben Davies
 * Class Name: DiscordConfig.java
 * Compiled Class Name: DiscordConfig.class
 * Date Created: 31/01/2018
 */


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder(toBuilder = true)
@Jacksonized
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DiscordConfig implements IDiscordConfig
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * This is used to connect to the discord api with your selected bot.
     */
    @Builder.Default
    private String token = "";


    /**
     * This is what will be used to determine if a discord message can be considered a command.
     */
    @Builder.Default
    private final String commandPrefix = "!";

    /**
     * This will be used to set the playing text on startup.
     */
    @Builder.Default
    private final String playingText = "{commandPrefix}help {cmdName}";

    @Builder.Default
    private String shutdownPassword = "password";


    /**
     * This will return the token.
     *
     * @return String
     */
    public String getToken()
    {
        return token;
    }

    /**
     * This will return the command prefix.
     *
     * @return String
     */
    public String getCommandPrefix()
    {
        return commandPrefix;
    }

    @Override
    public String getPlayingText()
    {
        return playingText.replace("{commandPrefix}", getCommandPrefix());
    }

    @Override
    public String getShutdownPassword()
    {
        return shutdownPassword;
    }
}
