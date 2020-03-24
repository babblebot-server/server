package uk.co.bjdavies.api.plugins;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This will inject for custom config for your plugin.
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginConfig {
    Class<? extends ICustomPluginConfig> value() default ICustomPluginConfig.class;

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Setup {
        boolean autoGenerate() default true;

        String fileName() default "";
    }

}
