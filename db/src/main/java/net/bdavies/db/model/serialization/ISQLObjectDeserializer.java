package net.bdavies.db.model.serialization;

import net.bdavies.db.model.IModelProperty;
import net.bdavies.db.model.Model;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface ISQLObjectDeserializer<T extends Model, R> {
    R deserialize(T model, String data, IModelProperty property);
}
