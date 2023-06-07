/*
 * MIT License
 *
 * Copyright (c) 2020 Ben Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.bdavies.babblebot.api.variables;


import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Component
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
