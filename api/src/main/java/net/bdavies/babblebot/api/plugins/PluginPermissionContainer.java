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

package net.bdavies.babblebot.api.plugins;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.babblebot.api.config.EPluginPermission;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Plugin Permission Container
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.21
 */
@Slf4j
public class PluginPermissionContainer extends ArrayList<EPluginPermission>
{
    public PluginPermissionContainer()
    {
        super();
    }

    public PluginPermissionContainer(Collection<EPluginPermission> list)
    {
        super();
        this.addAll(list);
    }

    public boolean hasPermission(EPluginPermission permission)
    {
        return stream().anyMatch(p -> p.equals(permission));
    }
}
