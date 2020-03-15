package uk.co.bjdavies.api.variables;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IVariableContainer {
    /**
     * This method will add all the variables (@Variable.class) that are in that class to the 2 Maps.
     *
     * @param clazz - The class you want to insert them from.
     */
    void addAllFrom(Class<?> clazz);

    /**
     * This will add a method to the container.
     *
     * @param name   - the name of the variable.
     * @param method - the method for the variable.
     */
    void addMethod(String name, Method method);


    /**
     * This will add a field to the container.
     *
     * @param name  - the name of the variable.
     * @param field - the field for the variable.
     */
    void addField(String name, Field field);

    /**
     * This will remove one from the container based on the name.
     *
     * @param name - The name of the variable.
     */
    void remove(String name);

    /**
     * This will return a field based on the name specified.
     *
     * @param name - the name of the Field you want to receive.
     * @return Field
     */
    Field getFieldVariable(String name);

    /**
     * This will return a method based on the name specified.
     *
     * @param name - the name of the Field you want to receive.
     * @return Method
     */
    Method getMethodVariable(String name);

    /**
     * This checks whether the variable exists in this container.
     *
     * @param name - the name of the variable.
     * @return Boolean
     */
    boolean exists(String name);
}
