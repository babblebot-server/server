package uk.co.bjdavies.api.command;

import java.lang.annotation.*;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CommandParams.class)
public @interface CommandParam {
    /**
     * The name of the parameter
     *
     * @return String
     */
    String value();

    /**
     * If the command is optional
     *
     * @return boolean
     */
    boolean optional() default true;

    /**
     * If the parameter requires a value or not e.g. -param or -param=true
     *
     * @return boolean
     */
    boolean canBeEmpty() default true;

    /**
     * This is an example value for the example's generator
     *
     * @return String
     */
    String exampleValue() default "";

}
