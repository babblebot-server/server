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

import net.bdavies.api.config.IPluginConfig;

/**
 * BabbleBot, open-source Discord Bot
 * Author: Ben Davies
 * Class Name: ModuleConfig.java
 * Compiled Class Name: ModuleConfig.class
 * Date Created: 31/01/2018
 */

public class PluginConfig implements IPluginConfig {
    /**
     * This is the location that the plugin's Jar file and configs are kept
     * Keep in mind there is a convention for the structure. Convention over Configuration approach.
     * e.g. AudioDJ -> AudioDJ.jar , AudioDJ.json
     */
    private String pluginLocation;

    /**
     * This is the class path that the class that implements
     */
    private String pluginClassPath = "";

    /**
     * This is the type of module it is either "js" or "java" <strong>default</strong> is java
     */
    private String pluginType = "java";


    @Override
    public String getPluginLocation() {
        return pluginLocation;
    }

    @Override
    public String getPluginClassPath() {
        return pluginClassPath;
    }

    @Override
    public String getPluginType() {
        return pluginType;
    }
}
