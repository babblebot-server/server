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

package net.bdavies.babblebot.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.babblebot.api.IApplication;
import net.bdavies.babblebot.api.dto.Response;
import net.bdavies.babblebot.api.dto.ResponseBag;
import net.bdavies.babblebot.api.events.IEventDispatcher;
import net.bdavies.babblebot.api.events.PluginAddedEvent;
import net.bdavies.babblebot.api.events.PluginRemovedEvent;
import net.bdavies.babblebot.api.plugins.IPluginContainer;
import net.bdavies.babblebot.dto.AllPluginsResponse;
import net.bdavies.babblebot.dto.CreatePluginRequest;
import net.bdavies.babblebot.dto.GetPluginResponse;
import net.bdavies.babblebot.plugins.PluginModel;
import net.bdavies.babblebot.plugins.PluginModelRepository;
import net.bdavies.babblebot.plugins.importing.ImportPluginFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Plugin Service layer
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PluginService
{
    private final PluginModelRepository pluginModelRepository;
    private final IPluginContainer pluginContainer;
    private final IApplication application;
    private final IEventDispatcher eventDispatcher;

    @SneakyThrows
    public ResponseBag createPlugin(CreatePluginRequest request)
    {
        return ResponseBag.from(uid -> {
            val opt = pluginModelRepository.findByName(request.getName());

            if (opt.isPresent())
            {
                log.warn("[{}] plugin ({}) already exists", uid, request.getName());
                return Response.from("Plugin already exists", HttpStatus.NOT_ACCEPTABLE.value());
            }

            val model = PluginModel.builder()
                    .pluginType(request.getType())
                    .name(request.getName())
                    .pluginPermissions(request.getPermissions())
                    .classPath(request.getClassPath())
                    .namespace(request.getNamespace())
                    .fileData(request.getPlugin().getBytes())
                    .build();


            pluginModelRepository.save(model);

            ImportPluginFactory.importPlugin(model, application)
                    .subscribe(pObj -> pluginContainer.addPlugin(pObj, model));

            eventDispatcher.dispatch(new PluginAddedEvent(model));

            return Response.from("Plugin added to the system", HttpStatus.CREATED.value());
        });
    }

    public ResponseBag getAllPlugins()
    {
        return ResponseBag.from(uid -> AllPluginsResponse
                .from(Response.from("Plugins"), pluginModelRepository.findAll()));
    }

    public ResponseBag getPlugin(String name)
    {
        return ResponseBag.from(uid -> {
            val opt = pluginModelRepository.findByName(name);
            if (opt.isEmpty())
            {
                return Response.from(name + " plugin not found", HttpStatus.NOT_FOUND.value());
            } else
            {
                return GetPluginResponse.from(Response.from(name + " plugin"),
                        opt.get());
            }
        });
    }

    public ResponseBag updatePlugin(String name, CreatePluginRequest request)
    {
        return ResponseBag.from(uid -> {
            val opt = pluginModelRepository.findByName(name);

            if (opt.isEmpty())
            {
                log.warn("[{}] plugin ({}) doesn't exist", uid, name);
                return Response.from("Plugin doesn't exist", HttpStatus.NOT_FOUND.value());
            }

            PluginModel model = opt.get();

            if (request.getName() != null && !request.getName().trim().equals(""))
            {
                model.setName(request.getName());
            }

            if (request.getPlugin() != null)
            {
                model.setFileData(request.getPlugin().getBytes());
            }

            if (request.getType() != null)
            {
                model.setPluginType(request.getType());
            }

            if (request.getNamespace() != null)
            {
                model.setNamespace(request.getNamespace());
            }

            if (request.getClassPath() != null && !request.getClassPath().trim().equals(""))
            {
                model.setClassPath(request.getClassPath());
            }

            if (!request.getPermissions().isEmpty())
            {
                model.setPluginPermissions(request.getPermissions());
            }

            pluginModelRepository.save(model);

            pluginContainer.removePlugin(name);
            eventDispatcher.dispatch(new PluginRemovedEvent(name));

            ImportPluginFactory.importPlugin(model, application)
                    .subscribe(pObj -> pluginContainer.addPlugin(pObj, model));

            eventDispatcher.dispatch(new PluginAddedEvent(model));

            return Response.from("Plugin " + name + " updated");
        });
    }

    public ResponseBag deletePlugin(String name)
    {
        return ResponseBag.from(uid -> {
            val opt = pluginModelRepository.findByName(name);
            if (opt.isEmpty())
            {
                return Response.from(name + " plugin not found", HttpStatus.NOT_FOUND.value());
            } else
            {
                pluginModelRepository.delete(opt.get());
                pluginContainer.removePlugin(name);
                eventDispatcher.dispatch(new PluginRemovedEvent(name));
                return GetPluginResponse.from(Response.from(name + " plugin delete"),
                        opt.get());
            }
        });
    }
}
