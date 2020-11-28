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

package net.bdavies.db.model.hooks;


import net.bdavies.db.model.Model;

/**
 * This will be called when a model is first created but not when populated from the db
 * <p>&nbsp;</p>
 * <strong>Lifecycle:</strong>
 * <ul>
 *     <li>The fields get setup for db processing</li>
 *     <li>The fields get any data from the DB.create(Model.class, Map <-)</li>
 *     <li>When {@link Model#save()} is used</li>
 *     <li>{@link #onCreate()} is then called</li>
 * </ul>
 * <p>&nbsp;</p>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 * @author <a href="mailto:me@bdavies.net">me@bdavies (Ben Davies)</a>
 */
public interface ICreateHook {
    /**
     * This will be called when you run DB.create(Model.class);
     */
    void onCreate();
}
