package uk.co.bjdavies.api.command;

import javax.annotation.CheckReturnValue;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CheckReturnValue
public @interface Command {
    /**
     * The aliases of the command.
     *
     * @return String[]
     */
    String[] aliases() default {};


    /**
     * The Description for the command.
     *
     * @return String
     */
    String description() default "";

    /**
     * The Usage for the command.
     *
     * @return String
     */
    String usage() default "";


    /**
     * The type of command (Terminal, Discord, All).
     *
     * @return String
     */
    String type() default "Discord";

    /**
     * A list of required params for the command.
     *
     * @return String[]
     */
    String[] requiredParams() default "";
}
