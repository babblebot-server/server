package uk.co.bjdavies.api.command;

import javax.annotation.CheckReturnValue;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is used for plugins and will be used for a method
 * Return types valid for a function are:-
 * String,
 * Mono<String>
 * Mono<EmbedSpec>
 * Flux<String>
 *
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
     * @deprecated automatically generated
     */
    String usage() default "";

    /**
     * This determines where the command requires a value or not
     *
     * @return boolean
     */
    boolean requiresValue() default false;

    /**
     * This will be used for the examples generator.
     *
     * @return String
     */
    String exampleValue() default "";

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
     * @deprecated please use {@link CommandParam}
     */
    String[] requiredParams() default "";
}
