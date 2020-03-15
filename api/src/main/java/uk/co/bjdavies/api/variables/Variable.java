package uk.co.bjdavies.api.variables;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation will declare to the plugin parser that either the field or the method will be used as a variable
 * <p>
 * Usages for variables
 * <p>
 * Field:
 * {@code @uk.co.bjdavies.api.variables.Variable private String testVariable = "I am Test uk.co.bjdavies.api.variables.Variable";}
 * <p>
 * You would then declare this in command as $(testVar)
 * <p>
 * <p>
 * Method:
 * {@code @uk.co.bjdavies.api.variables.Variable private String printString(String s) {
 * return s;
 * }}
 * <p>
 * You would then declare this in a command as $(printString(Hi))
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Variable {
}
