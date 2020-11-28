package net.bdavies.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.bdavies.db.model.hooks.IPropertyUpdateHook;
import net.bdavies.db.model.serialization.ISQLObjectDeserializer;
import net.bdavies.db.model.serialization.ISQLObjectSerializer;

import java.lang.reflect.Field;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@SuppressWarnings("ClassWithoutLogger")
@AllArgsConstructor
@Getter
@ToString
public class ModelProperty implements IModelProperty {
    private final Class<?> type;
    private final Field field;
    private final String name;
    private final boolean isProtected;
    private final boolean isPrimary;
    private final boolean increments;
    private final Class<? extends ISQLObjectSerializer<Model, Object>> serializer;
    private final Class<? extends ISQLObjectDeserializer<Model, Object>> deSerializer;
    private final Class<? extends IPropertyUpdateHook<Model, Object>> onUpdate;
    @Setter
    private String previousValue;
    private final boolean isRelational;
    private final Relationship relationship;
}
