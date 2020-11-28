package net.bdavies.db.model;

import net.bdavies.db.model.serialization.ISQLObjectDeserializer;
import net.bdavies.db.model.serialization.ISQLObjectSerializer;

import java.lang.reflect.Field;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface IModelProperty {

    Class<?> getType();

    Field getField();

    String getName();

    boolean isProtected();

    boolean isPrimary();

    boolean isIncrements();

    Class<? extends ISQLObjectSerializer<Model, Object>> getSerializer();

    Class<? extends ISQLObjectDeserializer<Model, Object>> getDeSerializer();
}
