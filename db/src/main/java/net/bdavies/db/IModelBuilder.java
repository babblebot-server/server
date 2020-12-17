package net.bdavies.db;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IModelBuilder<T extends Model> {
    /**
     * Return a collection of Type T
     *
     * @return {@link Collection}
     */
    <E extends Model> List<E> get();

    /**
     * Return a collection of Type T
     *
     * @param columns the columns you want to specify * by default
     * @return {@link Collection}
     */
    <E extends Model> List<E> get(String... columns);


    /**
     * Return the first element in the collection
     *
     * @return T
     */
    <E extends Model> Optional<E> first();

    /**
     * Return the first element in the collection
     *
     * @param columns the columns you want to specify * by default
     * @return T
     */
    <E extends Model> Optional<E> first(String... columns);

    /**
     * Find a element in the collection.
     *
     * @param id the id you want to retrieve
     * @return {@link Optional}
     */
    <E extends Model> Optional<E> find(int id);

    /**
     * Find a element in the collection.
     *
     * @param id      the id you want to retrieve
     * @param columns the columns you want to specify * by default
     * @return {@link Optional}
     */
    <E extends Model> Optional<E> find(int id, String... columns);

    /**
     * This will return a model throw an exception
     *
     * @param id the id you want to retrieve
     * @return {@link Model}
     * @throws NullPointerException if it cant find T.
     */
    <E extends Model> E findOrFail(int id) throws NullPointerException;

    /**
     * This will return a model throw an exception
     *
     * @param id      the id you want to retrieve
     * @param columns the columns you want to specify * by default
     * @return {@link Model}
     * @throws NullPointerException if it cant find T.
     */
    <E extends Model> E findOrFail(int id, String... columns) throws NullPointerException;

    IModelBuilder<T> orderBy(String column);

    IModelBuilder<T> reverse();

    IModelBuilder<T> limit(int number);

    IModelBuilder<T> select(String... columns);

    IModelBuilder<T> where(String key, Object value);

    IModelBuilder<T> where(String key, Comparator comparator, Object value);

    IModelBuilder<T> where(WhereStatement statement);

    IModelBuilder<T> and(WhereStatement... statement);

    IModelBuilder<T> or(WhereStatement... statement);

    int count();

    boolean exists();

    boolean doesntExist();

    String buildQuery();
}
