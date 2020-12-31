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

package net.bdavies.db.obj;

import net.bdavies.db.model.Model;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface IQueryObject extends IBaseBuilder, IGroupableWhereBuilder<IQueryObject> {
    @SuppressWarnings("UnusedReturnValue")
    IQueryObject columns(String... cols);

    <T extends Model> Set<T> get(Class<T> clazz);

    Set<Map<String, String>> get();

    boolean delete();
    boolean insert(Map<String, String> insertValues);
    int insertGetId(String idColName, Map<String, String> insertValues);
    int insertGetId(Map<String, String> insertValues);
    Map<String, String> insertGet(Map<String, String> insertValues);
    <T extends Model> T insertGet(Map<String, String> insertValues, Class<T> clazz);
    boolean update(Map<String, String> updateValues);
    Set<Map<String, String>> updateGet(Map<String, String> updateValues);
    <T extends Model> Set<T> updateGet(Map<String, String> updateValues, Class<T> clazz);
    <T extends Model> IQueryObject addModelPredicate(Predicate<T> wherePredicate);
}
