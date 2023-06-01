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

import net.bdavies.api.IApplication;
import net.bdavies.api.variables.IVariableContainer;
import net.bdavies.api.variables.Variable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BabbleBot, open-source Discord Bot
 * Author: Ben Davies
 * Class Name: VariableParser.java
 * Compiled Class Name: VariableParser.class
 * Date Created: 02/02/2018
 */
public class VariableParser {

    /**
     * This is the Variable types and there is 2 of them normal variable and a variable that is a function.
     */
    private final int VARIABLE = 0;
    private final int FUNCTION = 1;
    /**
     * The application instance.
     */
    private final IApplication application;
    /**
     * This is the string that has been parsed and will get returned to the user.
     */
    private String parsedString;


    /**
     * This is where the variables get parsed.
     *
     * @param commandResponse - The response of the command.
     * @param application     - The application instance.
     */
    public VariableParser(String commandResponse, IApplication application) {
        this.application = application;

        IVariableContainer container = application.getVariableContainer();

        parsedString = commandResponse;
        String[] variablesFound = findAllVariables(new DollarSignStrategy(), commandResponse);
        parsedString = removeAllVariableStrategyTags(new DollarSignStrategy(), commandResponse);
        for (String variable : variablesFound) {
            int type = getVariableType(variable);

            if (type == VARIABLE) {

                boolean exists = container.exists(variable);

                if (exists) {

                    Field field = container.getFieldVariable(variable);
                    field.setAccessible(true);
                    Class<?> targetType = field.getDeclaringClass();
                    try {
                        Object objectValue = targetType.newInstance();
                        Object obj = field.get(objectValue);

                        parsedString = parsedString.replace(variable, obj.toString());

                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isModuleVariable(variable)) {
                        Field field = getModuleVariableField(variable);
                        assert field != null;
                        field.setAccessible(true);
                        try {
                            Module module = getModuleFromVariable(variable);
                            Object obj = field.get(module);

                            parsedString = parsedString.replace(variable, obj.toString());

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else if (type == FUNCTION) {
                boolean exists = container.exists(parseMethodName(variable));

                if (exists) {
                    Method method = container.getMethodVariable(parseMethodName(variable));
                    method.setAccessible(true);

                    Class<?> declaredClass = method.getDeclaringClass();

                    try {
                        Object object = declaredClass.newInstance();

                        Object returnVal;
                        if (!hasMethodGotParams(variable))
                            returnVal = method.invoke(object);
                        else {
                            Object[] params = getMethodParams(variable);
                            returnVal = method.invoke(object, params);
                        }

                        if (returnVal != null) {
                            parsedString = parsedString.replace(variable, returnVal.toString());
                        }

                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isModuleVariable(variable)) {
                        Method method = getModuleVariableMethod(variable);
                        assert method != null;
                        method.setAccessible(true);


                        try {
                            Module module = getModuleFromVariable(variable);

                            Object returnVal;
                            if (!hasMethodGotParams(variable))
                                returnVal = method.invoke(module);
                            else {
                                Object[] params = getMethodParams(variable);
                                returnVal = method.invoke(module, params);
                            }

                            if (returnVal != null) {
                                parsedString = parsedString.replace(variable, returnVal.toString());
                            }

                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * This returns if a variable used is one used in a module.
     *
     * @param variable - This is the variable being parsed.
     * @return boolean
     */
    private boolean isModuleVariable(String variable) {
        if (variable.matches("[a-zA-Z0-9]+\\.[a-zA-Z0-9()., ]+")) {
            String moduleName = variable.substring(0, variable.indexOf("."));
//            if (application.getModuleContainer().doesModuleExist(moduleName))
//            {
//                return true;
//            }
            return false;
        }

        return false;
    }

    /**
     * This will return a Field from the module that is being used.
     *
     * @param variable - The variable being parsed.
     * @return Field
     */
    private Field getModuleVariableField(String variable) {
        Class<? extends Module> moduleClass = getModuleFromVariable(variable).getClass();

        try {
            Field field = moduleClass.getDeclaredField(variable.substring(variable.indexOf(".") + 1));

            if (field.isAnnotationPresent(Variable.class)) {
                return field;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * This will return a Method from the module that is being used.
     *
     * @param variable - The variable being parsed.
     * @return Method
     */
    private Method getModuleVariableMethod(String variable) {
        Class<? extends Module> moduleClass = getModuleFromVariable(variable).getClass();

        try {

            Method method = moduleClass.getDeclaredMethod(parseMethodName(variable.substring(variable.indexOf(".") + 1)));

            if (method.isAnnotationPresent(Variable.class)) {
                return method;
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This will return a Module.
     *
     * @param variable - The variable being parsed.
     * @return Module
     */
    private Module getModuleFromVariable(String variable) {
//        return application.getModuleContainer().getModule(variable.substring(0, variable.indexOf(".")));
        return null;
    }


    /**
     * This parses the parameters of the variable passed by the command response and will make an Object[] of all the params.
     * Which will get passed into the method that gets invoked.
     *
     * @param variable - The variable name that is being parsed e.g. getRandomGIF(example)
     * @return Object[]
     */
    private Object[] getMethodParams(String variable) {
        List<String> params = new ArrayList<>();
        List<Object> objects = new ArrayList<>();

        Pattern pattern = Pattern.compile("(([a-zA-Z0-9. ]+),|([a-zA-Z0-9. ]+))");
        Matcher matcher = pattern.matcher(variable.substring(variable.indexOf("(") + 1, variable.indexOf(")")));
        while (matcher.find()) {
            params.add(matcher.group(1).replace(",", ""));
        }

        for (String param : params) {
            if (isBoolean(param)) {
                objects.add(Boolean.parseBoolean(param));
            } else if (isFloat(param)) {
                objects.add(Float.parseFloat(param));
            } else if (isInteger(param)) {
                objects.add(Integer.parseInt(param));
            } else if (param.contains("\"")) {
                objects.add(param.replace("\"", ""));
            } else {
                objects.add(param);
            }
        }


        return objects.toArray(new Object[0]);
    }

    /**
     * This checks of the method parameter is a boolean.
     *
     * @param val - The value to be checked.
     * @return boolean
     */
    private boolean isBoolean(String val) {
        return val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false");
    }

    /**
     * This checks of the method parameter is a float.
     *
     * @param val - The value to be checked.
     * @return boolean
     */
    private boolean isFloat(String val) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Float.parseFloat(val);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * This checks of the method parameter is a integer.
     *
     * @param val - The value to be checked.
     * @return boolean
     */
    private boolean isInteger(String val) {
        try {
            Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * This takes the variable and will return method name without the () or (params...) at the end.
     *
     * @param variable - The variable being parsed.
     * @return String
     */
    private String parseMethodName(String variable) {
        if (!hasMethodGotParams(variable)) {
            return variable.replace("()", "");
        }

        return variable.replaceFirst("\\([a-zA-Z0-9()., ]+\\)", "");
    }

    /**
     * This will return true if the variable being parsed has params.
     *
     * @param method - The variable / method being parsed.
     * @return boolean
     */
    private boolean hasMethodGotParams(String method) {
        return method.matches("[a-zA-Z0-9.]+\\([a-zA-Z0-9()., ]+\\)");
    }

    /**
     * This will remove the tags that the variable strategy uses e.g. the DollarSignStrategy uses $() and it will remove that from the variable.
     *
     * @param variableStrategy - The variable strategy that determines the tags that are used and how the variable is parsed.
     * @param commandResponse  - The command response that is made.
     * @return String
     */
    private String removeAllVariableStrategyTags(VariableStrategy variableStrategy, String commandResponse) {
        return variableStrategy.removeTagsFromString(commandResponse);
    }


    /**
     * This will use the strategyProvided to parse the variables from the string that needs to be parsed.
     *
     * @param variableStrategy - The strategy in which to parse the variables.
     * @param commandResponse  - The response from the command.
     * @return String[]
     */
    private String[] findAllVariables(VariableStrategy variableStrategy, String commandResponse) {
        return variableStrategy.parseAllVariables(commandResponse);
    }

    /**
     * This is to determine what type of command variable it is.
     *
     * @param variable - The variable from the command response.
     * @return int
     */
    private int getVariableType(String variable) {
        return (variable.contains("(") && variable.contains(")")) ? FUNCTION : VARIABLE;
    }

    /**
     * This will return the parsed string.
     *
     * @return String
     */
    @Override
    public String toString() {
        return parsedString;
    }
}
