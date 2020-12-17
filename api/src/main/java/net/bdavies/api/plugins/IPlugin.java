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

package net.bdavies.api.plugins;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IPlugin {
    /**
     * This is the name of the Plugin.
     *
     * @return String
     */
    String getName();

    /**
     * This is the version of the Plugin.
     *
     * @return String
     * @deprecated Not required for plugin processing
     */
    String getVersion();

    /**
     * This is the author of the Plugin.
     *
     * @return String
     */
    String getAuthor();

    /**
     * This is the minimum version of the server that the plugin can run on. Default: 1
     *
     * @return String
     */
    String getMinimumServerVersion();

    /**
     * This is the maximum version of the server that the plugin can run on. Default: 0 (no limit)
     *
     * @return String
     */
    String getMaximumServerVersion();

    /**
     * This is the namespace for your commands
     * Please don't use ""
     * <p>
     * Because if it clashes with any commands your commands will not be added to system.
     *
     * @return String
     */
    String getNamespace();
}
