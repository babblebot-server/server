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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public abstract class QueryBuilder<T extends IDBRecord> extends BaseBuilder implements IQueryBuilder<T> {

    protected int limit = -1;
    protected String orderColumn = "";
    protected boolean reverseOrder = false;
    private Class<T> mapToModel;

    public QueryBuilder(String primaryKey, String tableName, IConnection connection) {
        super(primaryKey, tableName, connection);
    }

    public QueryBuilder(String primaryKey, String tableName, IConnection connection, Class<T> model) {
        this(primaryKey, tableName, connection);
        this.mapToModel = model;
    }

    @SneakyThrows
    @Override
    public List<T> get(String... columns) {
        select(columns);
        if (mapToModel == null) {
            return connection.executeQuery(this);
        } else {
            if (!Arrays.asList(this.selectColumns).contains("*")) {
                log.warn("For models you need to select all column or you  will get runtime errors. Changing back to *");
            }
            select("*");
            return covertListToModels(connection.executeQuery(this));
        }
    }

    private List<T> covertListToModels(List<T> executeQuery) {
        List<T> models = new ArrayList<>();

        for (T t : executeQuery) {
            try {
                T model = mapToModel.getDeclaredConstructor().newInstance();
                model.setData(t.getData());
                models.add(model);
            } catch (Exception e) {
                log.error("Unable to map model", e);
            }
        }

        return models;
    }

    @Override
    public List<T> get() {
        return get("*");
    }

    @Override
    public Optional<T> first() {
        return first("*");
    }

    @Override
    public Optional<T> first(String... columns) {
        limit(1);
        List<T> rows = get(columns);
        return rows.stream().findFirst();
    }

    @Override
    public IQueryBuilder<T> where(String key, Object value) {
        //noinspection unchecked
        return (IQueryBuilder<T>) super.where(key, value);
    }

    @Override
    public IQueryBuilder<T> where(String key, Comparator comparator, Object value) {
        //noinspection unchecked
        return (IQueryBuilder<T>) super.where(key, comparator, value);
    }

    @Override
    public IQueryBuilder<T> where(WhereStatement statement) {
        //noinspection unchecked
        return (IQueryBuilder<T>) super.where(statement);
    }

    @Override
    public IQueryBuilder<T> and(WhereStatement... statement) {
        //noinspection unchecked
        return (IQueryBuilder<T>) super.and(statement);
    }

    @Override
    public IQueryBuilder<T> or(WhereStatement... statement) {
        //noinspection unchecked
        return (IQueryBuilder<T>) super.or(statement);
    }

    @Override
    public Optional<T> find(int id) {
        return find(id, "*");
    }

    @Override
    public Optional<T> find(int id, String... columns) {
        //noinspection unchecked
        return where(primaryKey, String.valueOf(id)).first(columns);
    }

    @Override
    public T findOrFail(int id) throws NullPointerException {
        return findOrFail(id, "*");
    }

    @Override
    public T findOrFail(int id, String... columns) throws NullPointerException {
        Optional<T> obj = find(id, columns);
        if (obj.isPresent()) {
            return obj.get();
        }
        throw new NullPointerException("Cannot find " + primaryKey + ": " + id);
    }

    @Override
    public IQueryBuilder<T> orderBy(String column) {
        this.orderColumn = column;
        return this;
    }

    @Override
    public IQueryBuilder<T> reverse() {
        this.reverseOrder = true;
        return this;
    }

    @Override
    public IQueryBuilder<T> limit(int number) {
        this.limit = number;
        return this;
    }

    @Override
    public IQueryBuilder<T> select(String... columns) {
        this.selectColumns = columns;
        return this;
    }

    @Override
    public int count() {
        return get().size();
    }

    @Override
    public boolean exists() {
        return count() > 0;
    }

    @Override
    public boolean doesntExist() {
        return !exists();
    }

    public abstract String buildQuery();
}
