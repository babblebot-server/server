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

package net.bdavies.db.mock;

import lombok.SneakyThrows;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.command.ICommandDispatcher;
import uk.co.bjdavies.api.config.IConfig;
import uk.co.bjdavies.api.plugins.IPluginContainer;
import uk.co.bjdavies.api.variables.IVariableContainer;

import java.lang.reflect.InvocationTargetException;

/**
 * This is a mock of the server application
 *
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
public class ApplicationMock implements IApplication {

    @Override
    public <T> T get(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public IConfig getConfig() {
        return null;
    }

    @Override
    public ICommandDispatcher getCommandDispatcher() {
        return null;
    }

    @Override
    public IVariableContainer getVariableContainer() {
        return null;
    }

    @Override
    public IPluginContainer getPluginContainer() {
        return null;
    }

    @Override
    public String getServerVersion() {
        return null;
    }

    @Override
    public void shutdown(int timeout) {

    }

    @Override
    public boolean hasArgument(String argument) {
        return false;
    }

    @Override
    public void restart() {

    }
}
