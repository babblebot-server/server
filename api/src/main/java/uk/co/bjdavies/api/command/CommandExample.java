package uk.co.bjdavies.api.command;

import java.lang.annotation.*;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CommandExamples.class)
public @interface CommandExample {
    String value();
}
