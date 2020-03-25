package uk.co.bjdavies.api.plugins;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {
    // This is the name of the plugin
    String value() default "";

    String author() default "babblebot-plugin-developer";

    String minServerVersion() default "0";

    String maxServerVersion() default "0";

    /**
     * Name space will be default to the value a.k.a name with a -.
     * e.g. color-
     *
     * @return String
     */
    String namespace() default "<bb-def-uniq>";

}
