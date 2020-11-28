package net.bdavies.db.model.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseSQLObjectSerializer {
    Class<? extends ISQLObjectSerializer<?, ?>> value();
}
