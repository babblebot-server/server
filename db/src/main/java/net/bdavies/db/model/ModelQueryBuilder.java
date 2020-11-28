package net.bdavies.db.model;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.query.QueryBuilder;

import java.util.Set;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class ModelQueryBuilder<T extends Model> extends QueryBuilder implements IModelQueryBuilder<T> {

    private final Class<T> modelClazz;

    public ModelQueryBuilder(Class<T> modelClazz) {
        super(ModelUtils.getTableName(modelClazz));
        this.modelClazz = modelClazz;
    }

    @Override
    public ModelQueryBuilder<T> where(String col, String val) {
        object.where(col, val);
        return this;
    }

    @Override
    public ModelQueryBuilder<T> and(String col, String val) {
        object.and(col, val);
        return this;
    }

    @Override
    public ModelQueryBuilder<T> or(String col, String val) {
        object.or(col, val);
        return this;
    }

    @Override
    public ModelQueryBuilder<T> where(String col, String operator, String val) {
        object.where(col, operator, val);
        return this;
    }

    @Override
    public ModelQueryBuilder<T> and(String col, String operator, String val) {
        object.and(col, operator, val);
        return this;
    }

    @Override
    public ModelQueryBuilder<T> or(String col, String operator, String val) {
        object.or(col, operator, val);
        return this;
    }

    public boolean delete() {
        Set<T> models = get(modelClazz);
        models.forEach(Model::delete);
        return true;
    }
}
