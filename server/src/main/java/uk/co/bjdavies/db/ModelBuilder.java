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
