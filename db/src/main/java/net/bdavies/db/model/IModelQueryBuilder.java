package net.bdavies.db.model;

import net.bdavies.db.obj.IBaseBuilder;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface IModelQueryBuilder<T> extends IBaseBuilder {
    @Override
    IModelQueryBuilder<T> where(String col, String val);

    @Override
    IModelQueryBuilder<T> and(String col, String val);

    @Override
    IModelQueryBuilder<T> or(String col, String val);

    @Override
    IModelQueryBuilder<T> where(String col, String operator, String val);

    @Override
    IModelQueryBuilder<T> and(String col, String operator, String val);

    @Override
    IModelQueryBuilder<T> or(String col, String operator, String val);


    boolean delete();
}
