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

package net.bdavies.babblebot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.babblebot.api.dto.ResponseBag;
import net.bdavies.babblebot.dto.CreatePluginRequest;
import net.bdavies.babblebot.service.PluginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Handling Plugins
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.22
 */
@Slf4j
@RestController
@RequestMapping("/v1/plugins")
@RequiredArgsConstructor
public class PluginController
{
    private final PluginService pluginService;

    @PostMapping("/")
    ResponseEntity<ResponseBag> createPlugin(@ModelAttribute CreatePluginRequest request)
    {
        val resp = pluginService.createPlugin(request);
        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @GetMapping("/")
    ResponseEntity<ResponseBag> getAllPlugins() {
        ResponseBag resp = pluginService.getAllPlugins();
        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @GetMapping("/{name}")
    ResponseEntity<ResponseBag> getPlugin(@PathVariable("name") String name) {
        ResponseBag resp = pluginService.getPlugin(name);
        return ResponseEntity.status(resp.getStatus()).body(resp);
    }


    @PutMapping("/{name}")
    ResponseEntity<ResponseBag> updatePlugin(@ModelAttribute CreatePluginRequest request, @PathVariable("name") String name) {
        ResponseBag resp = pluginService.updatePlugin(name, request);
        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @DeleteMapping("/{name}")
    ResponseEntity<ResponseBag> deletePlugin(@PathVariable("name") String name) {
        ResponseBag resp = pluginService.deletePlugin(name);
        return ResponseEntity.status(resp.getStatus()).body(resp);
    }
}
