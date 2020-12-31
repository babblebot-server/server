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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.Operator;
import net.bdavies.db.Order;
import net.bdavies.db.model.Model;
import net.bdavies.db.query.PreparedQuery;
import net.bdavies.db.query.QueryLink;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Getter
@Setter
@Slf4j
@NoArgsConstructor
@ToString
public abstract class QueryObject implements IQueryObject {

    protected List<String> columns = new LinkedList<>();
    protected List<WhereStatementObject> wheres = new LinkedList<>();
    protected boolean isFirstWhere = true;
    protected Map<String, String> values = new LinkedHashMap<>();
    protected PreparedQuery preparedQuery = new PreparedQuery();
    protected Map<String, Order> orderMap = new LinkedHashMap<>();

    @Override
    public IQueryObject columns(String... cols) {
        columns.addAll(Arrays.asList(cols));
        return this;
    }

    @Override
    public IQueryObject where(String col, String val) {

        return where(col, Operator.EQ, val);
    }

    @Override
    public IQueryObject and(String col, String val) {
        return and(col, Operator.EQ, val);
    }

    @Override
    public IQueryObject or(String col, String val) {
        return or(col, Operator.EQ, val);
    }

    @Override
    public IQueryObject where(String col, Operator operator, String val) {
        if (isFirstWhere) {
            wheres.add(new WhereStatementObject(col, operator, val, null));
            isFirstWhere = false;
        } else {
            and(col, operator, val);
        }
        return this;
    }

    @Override
    public IQueryObject and(String col, Operator operator, String val) {
        wheres.add(new WhereStatementObject(col, operator, val, QueryLink.AND));
        return this;
    }

    @Override
    public IQueryObject or(String col, Operator operator, String val) {
        wheres.add(new WhereStatementObject(col, operator, val, QueryLink.OR));
        return this;
    }

    @Override
    public IQueryObject group(Consumer<IQueryObject> builder)
    {
        return group(builder, QueryLink.AND);
    }

    @Override
    public IQueryObject group(IQueryObject object)
    {
        return (IQueryObject) group(object, QueryLink.AND);
    }

    @Override
    public <T extends Model> IQueryObject addModelPredicate(Predicate<T> wherePredicate)
    {
        preparedQuery.getModelPredicates().add(wherePredicate);
        return this;
    }

    @Override
    public Map<String, String> first() {
        return get().stream().findFirst().orElse(Map.of());
    }

    @Override
    public <R extends Model> R first(Class<R> clazz) {
        return get(clazz).stream().findFirst().orElse(null);
    }

    @Override
    public Map<String, String> last() {
        Set<Map<String, String>> objects = get();
        long count = objects.size();
        return objects.stream().skip(count - 1).findFirst().orElse(Map.of());
    }

    @Override
    public <R extends Model> R last(Class<R> clazz) {
        Set<R> objects = get(clazz);
        long count = objects.size();
        return objects.stream().skip(count - 1).findFirst().orElse(null);
    }


    @Override
    public IQueryObject orderBy(Map<String, Order> cols) {
        this.orderMap = cols;
        return this;
    }

    @Override
    public IQueryObject orderBy(String... cols) {
        this.orderMap = Arrays.stream(cols).collect(Collectors.toMap(m -> m, m -> Order.ASC));
        return this;
    }



    @Override
    public int insertGetId(String idColName, Map<String, String> insertValues) {
        return Integer.parseInt(insertGet(insertValues).get(idColName));
    }

    @Override
    public int insertGetId(Map<String, String> insertValues) {
        return insertGetId("id", insertValues);
    }

    @Override
    public Map<String, String> insertGet(Map<String, String> insertValues) {
        insert(insertValues);
        Set<Map<String, String>> objects = get();
        long count = objects.size();
        return objects.stream().skip(count - 1).findFirst().orElse(null);
    }

    @Override
    public <T extends Model> T insertGet(Map<String, String> insertValues, Class<T> clazz) {
        insert(insertValues);
        Set<T> objects = get(clazz);
        long count = objects.size();
        return objects.stream().skip(count - 1).findFirst().orElse(null);
    }



    @Override
    public Set<Map<String, String>> updateGet(Map<String, String> updateValues) {
        update(updateValues);
        return get();
    }

    @Override
    public <T extends Model> Set<T> updateGet(Map<String, String> updateValues, Class<T> clazz) {
        update(updateValues);
        return get(clazz);
    }
}
