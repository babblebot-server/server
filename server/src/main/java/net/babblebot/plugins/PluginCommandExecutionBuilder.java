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

package net.babblebot.plugins;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class PluginCommandExecutionBuilder {
    private String name;

    private Class<?> pluginClass;

    private Object[] args = new Object[0];

    private Class<?>[] parameterTypes = new Class[0];

    public PluginCommandExecutionBuilder(String name, Class<?> pluginClass) {
        this.name = name;
        this.pluginClass = pluginClass;
    }

    public PluginCommandExecutionBuilder setPluginClass(Class<?> pluginClass) {
        this.pluginClass = pluginClass;
        return this;
    }

    public PluginCommandExecutionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PluginCommandExecutionBuilder setArgs(Object... args) {
        this.args = args;
        return this;
    }

    public PluginCommandExecutionBuilder setParameterTypes(Class<?>... parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    public PluginCommandDefinition build() {
        return new PluginCommandDefinition() {
            public String getName() {
                return PluginCommandExecutionBuilder.this.name;
            }

            public Object[] getArgs() {
                return PluginCommandExecutionBuilder.this.args;
            }

            public Class<?>[] getParameterTypes() {
                return PluginCommandExecutionBuilder.this.parameterTypes;
            }

            public Class<?> getPluginClass() {
                return PluginCommandExecutionBuilder.this.pluginClass;
            }
        };
    }
}
