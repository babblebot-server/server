package net.bdavies.db.model.fields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This means it is protected from JSON Output.
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Protected {
}
