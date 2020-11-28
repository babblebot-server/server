package net.bdavies.db.model.serialization;

import net.bdavies.db.model.Model;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface ISQLSerializationObject<T extends Model, R> extends ISQLObjectSerializer<T, R>,
  ISQLObjectDeserializer<T, R> {
}
