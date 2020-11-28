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

package uk.co.bjdavies.db.Table;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: Property.java
 * Compiled Class Name: Property.class
 * Date Created: 08/02/2018
 */

public abstract class Property {


    /**
     * This determines if the field can be null.
     */
    protected boolean nullable = false;


    /**
     * This determines the defaultValue of the field.
     */
    protected String defaultValue = null;


    /**
     * This determines the name of the field.
     */
    protected String fieldName;


    /**
     * This will construct a property.
     *
     * @param fieldName - The name of the field.
     */
    public Property(String fieldName) {
        this.fieldName = fieldName;
    }


    /**
     * This will return if the property is nullable.
     *
     * @return boolean
     */
    public boolean isNullable() {
        return nullable;
    }

    public Property setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    /**
     * This will set the field to be a nullable field.
     *
     * @return Property
     */
    public Property nullable() {
        this.nullable = true;
        return this;
    }

    /**
     * This will return the default value of the field.
     *
     * @return String
     */
    public String getDefaultValue() {
        return defaultValue;
    }


    /**
     * This will set the default value of the field.
     *
     * @param defaultValue - The default value of the field.
     * @return Property
     */
    public Property defaultValue(Object defaultValue) {
        if (defaultValue != null) {
            this.defaultValue = String.valueOf(defaultValue);

        }
        return this;
    }


    /**
     * This will return the field name.
     *
     * @return String
     */
    public String getFieldName() {
        return fieldName;
    }


    /**
     * This will set the field name.
     *
     * @param fieldName - The new name of the field.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }


    /**
     * This will generate the ending of a property description with whether it has a default value and/or it is nullable.
     *
     * @return String
     */
    protected String getStringEnding() {
        return ((defaultValue != null) ? "DEFAULT " + defaultValue + " " : "") + ((nullable) ? "NULL" : "NOT NULL");
    }
}
