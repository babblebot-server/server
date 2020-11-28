package net.bdavies.db.model.serialization;

import net.bdavies.db.model.IModelProperty;
import net.bdavies.db.model.Model;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface ISQLObjectSerializer<T extends Model, R> {
    String serialize(T model, R data, IModelProperty property);

    default String getSQLColumnType() { return "varchar"; }
}
