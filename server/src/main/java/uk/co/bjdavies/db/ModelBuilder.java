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

package uk.co.bjdavies.db;

import uk.co.bjdavies.api.db.*;

import java.util.List;
import java.util.Optional;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class ModelBuilder<T extends Model> implements IModelBuilder<T> {

    private final IQueryBuilder<T> query;

    public ModelBuilder(String primaryKey, String tableName, IConnection connection, Class<T> model) {
        query = connection.createQueryBuilder(tableName, primaryKey, model);
    }

    @Override
    public <E extends Model> List<E> get() {
        //noinspection unchecked
        return (List<E>) query.get();
    }

    @Override
    public <E extends Model> List<E> get(String... columns) {
        //noinspection unchecked
        return (List<E>) query.get(columns);
    }

    @Override
    public <E extends Model> Optional<E> first() {
        return first("*");
    }

    @Override
    public <E extends Model> Optional<E> first(String... columns) {
        //noinspection unchecked
        return (Optional<E>) query.first(columns);
    }

    @Override
    public <E extends Model> Optional<E> find(int id) {
        return find(id, "*");
    }

    @Override
    public <E extends Model> Optional<E> find(int id, String... columns) {
        //noinspection unchecked
        return (Optional<E>) query.find(id, columns);
    }

    @Override
    public <E extends Model> E findOrFail(int id) throws NullPointerException {
        return findOrFail(id, "*");
    }

    @Override
    public <E extends Model> E findOrFail(int id, String... columns) throws NullPointerException {
        //noinspection unchecked
        return (E) query.findOrFail(id, columns);
    }

    @Override
    public IModelBuilder<T> orderBy(String column) {
        query.orderBy(column);
        return this;
    }

    @Override
    public IModelBuilder<T> reverse() {
        query.reverse();
        return this;
    }

    @Override
    public IModelBuilder<T> limit(int number) {
        query.limit(number);
        return this;
    }

    @Override
    public IModelBuilder<T> select(String... columns) {
        query.select(columns);
        return this;
    }

    @Override
    public IModelBuilder<T> where(String key, Object value) {
        query.where(key, value);
        return this;
    }

    @Override
    public IModelBuilder<T> where(String key, Comparator comparator, Object value) {
        query.where(key, comparator, value);
        return this;
    }

    @Override
    public IModelBuilder<T> where(WhereStatement statement) {
        query.where(statement);
        return this;
    }

    @Override
    public IModelBuilder<T> and(WhereStatement... statement) {
        query.and(statement);
        return this;
    }

    @Override
    public IModelBuilder<T> or(WhereStatement... statement) {
        query.or(statement);
        return this;
    }

    @Override
    public int count() {
        return query.count();
    }

    @Override
    public boolean exists() {
        return query.exists();
    }

    @Override
    public boolean doesntExist() {
        return query.doesntExist();
    }

    @Override
    public String buildQuery() {
        return query.buildQuery();
    }
}
