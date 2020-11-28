package net.bdavies.db.obj;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.query.PreparedQuery;
import net.bdavies.db.query.QueryLink;
import net.bdavies.db.query.QueryType;
import net.bdavies.db.dialect.connection.IConnection;

import java.util.*;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public abstract class QueryObject extends SQLObject implements IQueryObject {
    @NonNull
    protected String table;
    protected List<String> columns = new LinkedList<>();
    protected List<WhereStatementObject> wheres = new LinkedList<>();
    protected boolean isFirstWhere = true;
    protected Map<String, String> values = new LinkedHashMap<>();
    protected PreparedQuery preparedQuery = new PreparedQuery();

    @NonNull
    private final IConnection connection;

    @Override
    public IQueryObject columns(String... cols) {
        columns.addAll(Arrays.asList(cols));
        return this;
    }

    @Override
    public IQueryObject where(String col, String val) {

        return where(col, "=", val);
    }

    @Override
    public IQueryObject and(String col, String val) {
        return and(col, "=", val);
    }

    @Override
    public IQueryObject or(String col, String val) {
        return or(col, "=", val);
    }

    @Override
    public IQueryObject where(String col, String operator, String val) {
        if (isFirstWhere) {
            wheres.add(new WhereStatementObject(col, operator, val, null));
            isFirstWhere = false;
        } else {
            and(col, operator, val);
        }
        return this;
    }

    @Override
    public IQueryObject and(String col, String operator, String val) {
        wheres.add(new WhereStatementObject(col, operator, val, QueryLink.AND));
        return this;
    }

    @Override
    public IQueryObject or(String col, String operator, String val) {
        wheres.add(new WhereStatementObject(col, operator, val, QueryLink.OR));
        return this;
    }

    @Override
    public <T> Set<T> get(Class<T> clazz) {
        return connection.executeQuery(clazz, this.toSQLString(QueryType.SELECT, preparedQuery), preparedQuery);
    }

    @Override
    public Map<String, String> first() {
        return get().stream().findFirst().orElse(Map.of());
    }

    @Override
    public <R> R first(Class<R> clazz) {
        return get(clazz).stream().findFirst().orElse(null);
    }

    @Override
    public Map<String, String> last() {
        Set<Map<String, String>> objects = get();
        long count = objects.size();
        return objects.stream().skip(count - 1).findFirst().orElse(Map.of());
    }

    @Override
    public <R> R last(Class<R> clazz) {
        Set<R> objects = get(clazz);
        long count = objects.size();
        return objects.stream().skip(count - 1).findFirst().orElse(null);
    }

    @Override
    public Set<Map<String, String>> get() {
        return connection.executeQueryRaw(this.toSQLString(QueryType.SELECT, preparedQuery), preparedQuery);
    }

    @Override
    public boolean delete() {
        return connection.executeCommand(this.toSQLString(QueryType.DELETE, preparedQuery), preparedQuery);
    }

    @Override
    public boolean insert(Map<String, String> insertValues) {
        this.values = insertValues;
        return connection.executeCommand(this.toSQLString(QueryType.INSERT, preparedQuery), preparedQuery);
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
    public <T> T insertGet(Map<String, String> insertValues, Class<T> clazz) {
        insert(insertValues);
        Set<T> objects = get(clazz);
        long count = objects.size();
        return objects.stream().skip(count - 1).findFirst().orElse(null);
    }

    @Override
    public boolean update(Map<String, String> updateValues) {
        this.values = updateValues;
        return connection.executeCommand(this.toSQLString(QueryType.UPDATE, preparedQuery), preparedQuery);
    }

    @Override
    public Set<Map<String, String>> updateGet(Map<String, String> updateValues) {
        update(updateValues);
        return get();
    }

    @Override
    public <T> Set<T> updateGet(Map<String, String> updateValues, Class<T> clazz) {
        update(updateValues);
        return get(clazz);
    }
}
