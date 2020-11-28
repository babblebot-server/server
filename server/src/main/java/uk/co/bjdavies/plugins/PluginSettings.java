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

package uk.co.bjdavies.plugins;

import uk.co.bjdavies.api.plugins.IPluginSettings;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public class PluginSettings implements IPluginSettings {

    private final String name, author, minimumServerVersion, maximumServerVersion;
    private String namespace;

    public PluginSettings(String name, String author, String namespace, String minimumServerVersion, String maximumServerVersion) {
        this.name = name;
        this.author = author;
        this.namespace = namespace;
        this.minimumServerVersion = minimumServerVersion;
        this.maximumServerVersion = maximumServerVersion;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getMinimumServerVersion() {
        return minimumServerVersion;
    }

    @Override
    public String getMaximumServerVersion() {
        return maximumServerVersion;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
