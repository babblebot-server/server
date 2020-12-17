package net.bdavies.db;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IBaseBuilder {
    /**
     * This will return a QueryBuilder of the results from the where.
     *
     * @param key   the column name
     * @param value the value you are looking for
     * @return {@link IQueryBuilder}
     */
    IBaseBuilder where(String key, Object value);

    /**
     * This will return a QueryBuilder of the results from the where.
     *
     * @param key        the column name
     * @param comparator how you want to compare the the key and value
     * @param value      the value you are looking for
     * @return {@link IQueryBuilder}
     */
    IBaseBuilder where(String key, Comparator comparator, Object value);

    /**
     * This will return a QueryBuilder of the results from the where.
     *
     * @param statement {@link WhereStatement}
     * @return {@link IQueryBuilder}
     */
    IBaseBuilder where(WhereStatement statement);

    /**
     * This will return a QueryBuilder of the results from the where.
     *
     * @param statement {@link WhereStatement}
     * @return {@link IQueryBuilder}
     */
    IBaseBuilder and(WhereStatement... statement);

    /**
     * This will return a QueryBuilder of the results from the where.
     *
     * @param statement {@link WhereStatement}
     * @return {@link IQueryBuilder}
     */
    IBaseBuilder or(WhereStatement... statement);

}
