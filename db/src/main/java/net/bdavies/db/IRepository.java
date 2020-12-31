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

package net.bdavies.db;

import net.bdavies.db.model.Model;

import java.util.Map;
import java.util.function.Consumer;

/**
 * ..
 *
 * @param <A> Array type so what to use for an list of items back
 * @param <S> Single type so what to use for a single item back
 * @param <T> Just the type of the repo back
 *
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
interface IRepository<A, S, T, I extends Model>
{
    A getAll();
    S findFirst(Consumer<ModelWhereQueryBuilder<I>> builder);
    S findLast(Consumer<ModelWhereQueryBuilder<I>> builder);
    A find(Consumer<ModelWhereQueryBuilder<I>> builder);
    T create(Map<String, Object> values);
    T create();
    T createAndPersist(Map<String, Object> values);
    boolean delete(Consumer<ModelWhereQueryBuilder<I>> builder);
}
