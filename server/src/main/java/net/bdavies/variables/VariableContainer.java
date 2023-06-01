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

package net.bdavies.variables;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.variables.IVariableContainer;
import net.bdavies.api.variables.Variable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * BabbleBot, open-source Discord Bot
 * Author: Ben Davies
 * Class Name: VariableContainer.java
 * Compiled Class Name: VariableContainer.class
 * Date Created: 30/01/2018
 */
@Slf4j
@Component
public class VariableContainer implements IVariableContainer
{
    /**
     * This is the Map for all the field variables.
     */
    private final Map<String, Field> variableFields;


    /**
     * This is the Map for all the method variables.
     */
    private final Map<String, Method> variableMethods;


    /**
     * This is where the Maps get initialized with the HashMap implementation of Map.
     */
    public VariableContainer()
    {
        variableFields = new HashMap<>();
        variableMethods = new HashMap<>();
    }


    /**
     * This method will add all the variables (@Variable.class) that are in that class to the 2 Maps.
     *
     * @param clazz - The class you want to insert them from.
     */
    public void addAllFrom(Class<?> clazz)
    {
        for (Method method : clazz.getMethods())
        {
            if (method.isAnnotationPresent(Variable.class))
            {
                addMethod(method.getName(), method);
            }
        }

        for (Field field : clazz.getFields())
        {
            if (field.isAnnotationPresent(Variable.class))
            {
                variableFields.put(field.getName(), field);
            }
        }
    }

    /**
     * This will add a method to the container.
     *
     * @param name   - the name of the variable.
     * @param method - the method for the variable.
     */
    public void addMethod(String name, Method method)
    {
        if (variableMethods.containsKey(name) || variableMethods.containsValue(method))
        {
            log.error("The key or method is already in the container.");
        } else
        {
            variableMethods.put(name, method);
        }
    }


    /**
     * This will add a field to the container.
     *
     * @param name  - the name of the variable.
     * @param field - the field for the variable.
     */
    public void addField(String name, Field field)
    {
        if (variableFields.containsKey(name) || variableFields.containsValue(field))
        {
            log.error("The key or field is already in the container.");
        } else
        {
            variableFields.put(name, field);
        }
    }

    /**
     * This will remove one from the container based on the name.
     *
     * @param name - The name of the variable.
     */
    public void remove(String name)
    {
        if (variableFields.containsKey(name))
        {
            variableFields.remove(name);
        } else if (variableMethods.containsKey(name))
        {
            variableMethods.remove(name);
        } else
        {
            log.error("The name specified cannot be found inside this container.");
        }
    }

    /**
     * This will return a field based on the name specified.
     *
     * @param name - the name of the Field you want to receive.
     * @return Field
     */
    public Field getFieldVariable(String name)
    {
        if (!variableFields.containsKey(name))
        {
            log.error("Field cannot be found with the name specified in this container.");
        } else
        {
            return variableFields.get(name);
        }
        return null;
    }

    /**
     * This will return a method based on the name specified.
     *
     * @param name - the name of the Field you want to receive.
     * @return Method
     */
    public Method getMethodVariable(String name)
    {
        if (!variableMethods.containsKey(name))
        {
            log.error("Method cannot be found with the name specified in this container.");
        } else
        {
            return variableMethods.get(name);
        }
        return null;
    }

    /**
     * This checks whether the variable exists in this container.
     *
     * @param name - the name of the variable.
     * @return Boolean
     */
    public boolean exists(String name)
    {
        if (variableFields.containsKey(name))
        {
            return true;
        } else
        {
            return variableMethods.containsKey(name);
        }
    }

}
