package net.bdavies.db.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.DB;
import net.bdavies.db.obj.IQueryObject;
import net.bdavies.db.obj.QueryObject;

import java.util.Map;
import java.util.Set;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class QueryBuilder implements IQueryObject {

    protected final QueryObject object;

    public QueryBuilder(String table) {
        this.object = DB.buildObject(QueryObject.class, table);
        log.info("{}", object);
    }

    public QueryBuilder columns(String... cols) {
        object.columns(cols);
        return this;
    }

    @Override
    public IQueryObject where(String col, String val) {
        object.where(col, val);
        return this;
    }

    @Override
    public IQueryObject and(String col, String val) {
        object.and(col, val);
        return this;
    }

    @Override
    public IQueryObject or(String col, String val) {
        object.or(col, val);
        return this;
    }

    @Override
    public IQueryObject where(String col, String operator, String val) {
        object.where(col, operator, val);
        return this;
    }

    @Override
    public IQueryObject and(String col, String operator, String val) {
        object.and(col, operator, val);
        return this;
    }

    @Override
    public IQueryObject or(String col, String operator, String val) {
        object.or(col, operator, val);
        return this;
    }

    public <T> Set<T> get(Class<T> clazz) {
        return object.get(clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> first() {
        return object.first();
    }

    @Override
    public <R> R first(Class<R> clazz) {
        return object.first(clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> last() {
        return object.last();
    }

    @Override
    public <R> R last(Class<R> clazz) {
        return object.last(clazz);
    }

    @SuppressWarnings("unchecked")
    public Set<Map<String, String>> get() {
        return object.get();
    }

    @Override
    public boolean delete() {
        return object.delete();
    }

    @Override
    public boolean insert(Map<String, String> insertValues) {
        return object.insert(insertValues);
    }

    @Override
    public int insertGetId(String idColName, Map<String, String> insertValues) {
        return object.insertGetId(idColName, insertValues);
    }

    @Override
    public int insertGetId(Map<String, String> insertValues) {
        return object.insertGetId(insertValues);
    }

    @Override
    public Map<String, String> insertGet(Map<String, String> insertValues) {
        return object.insertGet(insertValues);
    }

    @Override
    public <T> T insertGet(Map<String, String> insertValues, Class<T> clazz) {
       return object.insertGet(insertValues, clazz);
    }

    @Override
    public boolean update(Map<String, String> updateValues) {
        return object.update(updateValues);
    }

    @Override
    public Set<Map<String, String>> updateGet(Map<String, String> updateValues) {
        return object.updateGet(updateValues);
    }

    @Override
    public <T> Set<T> updateGet(Map<String, String> updateValues, Class<T> clazz) {
        return object.updateGet(updateValues, clazz);
    }
}
