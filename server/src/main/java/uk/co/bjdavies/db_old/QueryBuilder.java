package uk.co.bjdavies.db_old;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import uk.co.bjdavies.api.db.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
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
