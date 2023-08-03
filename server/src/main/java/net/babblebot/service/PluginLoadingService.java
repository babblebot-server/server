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

package net.babblebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.IApplication;
import net.babblebot.api.config.EPluginPermission;
import net.babblebot.api.plugins.IPluginContainer;
import net.babblebot.core.CorePlugin;
import net.babblebot.plugins.PluginModel;
import net.babblebot.plugins.PluginModelRepository;
import net.babblebot.plugins.importing.ImportPluginFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for Loading Plugins
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PluginLoadingService
{
    private final IPluginContainer container;
    private final IApplication application;
    private final CorePlugin corePlugin;
    private final PluginModelRepository modelRepository;

    public void loadPlugins()
    {
        addCorePlugin();
        loadPluginsFromDatabase();
    }

    private void loadPluginsFromDatabase()
    {
        List<PluginModel> plugins = modelRepository.findAll();
        plugins.forEach(pluginModel -> ImportPluginFactory.importPlugin(pluginModel, application)
                .subscribe(pObj -> container.addPlugin(pObj, pluginModel)));
    }

    private void addCorePlugin()
    {
        container.addPlugin(corePlugin,
                PluginModel.builder()
                        .pluginPermissions(EPluginPermission.all())
                        .namespace("")
                        .build());
    }
}
