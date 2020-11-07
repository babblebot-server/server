package uk.co.bjdavies.api.db;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IQueryBuilder<T> extends IBaseBuilder {

    /**
     * Return a collection of Type T
     *
     * @return {@link Collection}
     */
    List<T> get();

    /**
     * Return a collection of Type T
     *
     * @param columns the columns you want to specify * by default
     * @return {@link Collection}
     */
    List<T> get(String... columns);


    /**
     * Return the first element in the collection
     *
     * @return T
     */
    Optional<T> first();

    /**
     * Return the first element in the collection
     *
     * @param columns the columns you want to specify * by default
     * @return T
     */
    Optional<T> first(String... columns);

    /**
     * Find a element in the collection.
     *
     * @param id the id you want to retrieve
     * @return {@link Optional}
     */
    Optional<T> find(int id);

    /**
     * Find a element in the collection.
     *
     * @param id      the id you want to retrieve
     * @param columns the columns you want to specify * by default
     * @return {@link Optional}
     */
    Optional<T> find(int id, String... columns);

    /**
     * This will return a model throw an exception
     *
     * @param id the id you want to retrieve
     * @return {@link Model}
     *
     * @throws NullPointerException if it cant find T.
     */
    T findOrFail(int id) throws NullPointerException;

    /**
     * This will return a model throw an exception
     *
     * @param id      the id you want to retrieve
     * @param columns the columns you want to specify * by default
     * @return {@link Model}
     *
     * @throws NullPointerException if it cant find T.
     */
    T findOrFail(int id, String... columns) throws NullPointerException;

    IQueryBuilder<T> orderBy(String column);

    IQueryBuilder<T> reverse();

    IQueryBuilder<T> limit(int number);

    IQueryBuilder<T> select(String... columns);

    @Override
    IQueryBuilder<T> where(String key, Object value);

    @Override
    IQueryBuilder<T> where(String key, Comparator comparator, Object value);

    @Override
    IQueryBuilder<T> where(WhereStatement statement);

    @Override
    IQueryBuilder<T> and(WhereStatement... statement);

    @Override
    IQueryBuilder<T> or(WhereStatement... statement);

    int count();

    boolean exists();

    boolean doesntExist();

    String buildQuery();
}
