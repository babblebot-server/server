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

package uk.co.bjdavies.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import lombok.Getter;
import net.bdavies.api.config.IConfig;
import net.bdavies.api.config.IDiscordConfig;
import net.bdavies.api.config.IHttpConfig;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class ConfigModule extends AbstractModule {

    @Getter
    private final IConfig config;


    public ConfigModule(String path) {
        this.config = ConfigFactory.makeConfig(path);
    }

    @Override
    protected void configure() {
        bind(IConfig.class).toInstance(this.config);
    }

    @Provides
    private IDiscordConfig provideDiscordConfig(IConfig config) {
        return config.getDiscordConfig();
    }

    @Provides
    private IHttpConfig provideHttpConfig(IConfig config) {
        return config.getHttpConfig();
    }
}
