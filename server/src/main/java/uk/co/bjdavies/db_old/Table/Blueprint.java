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

package uk.co.bjdavies.db_old.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BabbleBot, open-source Discord Bot
 * Author: Ben Davies
 * Class Name: Blueprint.java
 * Compiled Class Name: Blueprint.class
 * Date Created: 07/02/2018
 */

public abstract class Blueprint {

    /**
     * This will hold all the fields in the table's description.
     */
    private final List<Property> propertyList = new ArrayList<>();
    /**
     * This will hold all the fields that require to be unique.
     */
    private final List<String> uniqueCols = new ArrayList<>();
    /**
     * This will hold which field is the primary key.
     */
    private String primaryKey = "";

    /**
     * This is where you describe the table.
     */
    public abstract void setup();


    /**
     * This is a integer property that automatically increments by 1.
     *
     * @param fieldName - The name of the new field.
     * @return IntegerProperty
     */
    public IntegerProperty increments(String fieldName) {
        IntegerProperty property = new IntegerProperty(fieldName, true);
        propertyList.add(property);
        return property;
    }


    /**
     * This is a string property.
     *
     * @param fieldName - The name of the new field.
     * @param charLimit - The character limit.
     * @return StringProperty
     */
    public StringProperty string(String fieldName, int charLimit) {
        StringProperty property = new StringProperty(fieldName, charLimit);
        propertyList.add(property);
        return property;
    }


    /**
     * This is a string property.
     *
     * @param fieldName - The name of the new field.
     * @return StringProperty
     */
    public StringProperty string(String fieldName) {
        StringProperty property = new StringProperty(fieldName);
        propertyList.add(property);
        return property;
    }

    /**
     * This is a integer property.
     *
     * @param fieldName - The name of the new field.
     * @return IntegerProperty
     */
    public IntegerProperty integer(String fieldName) {
        IntegerProperty property = new IntegerProperty(fieldName, false);
        propertyList.add(property);
        return property;
    }

    /**
     * This sets the primary key to the specified field name.
     *
     * @param fieldName - The name of the field to be set as primary.
     */
    public void primaryKey(String fieldName) {
        this.primaryKey = fieldName;
    }

    /**
     * This sets the unique keys to the fields specified.
     *
     * @param fieldNames - The names of the fields to be classed as unique.
     */
    public void uniqueKeys(String... fieldNames) {
        Collections.addAll(uniqueCols, fieldNames);
    }


    /**
     * This will return the description of the new table.
     *
     * @return String.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        propertyList.stream().forEach(e -> {

            if (e.fieldName.toLowerCase().equals(primaryKey)) {
                stringBuilder.append(e.toString().replace("AUTOINCREMENT", "PRIMARY KEY AUTOINCREMENT"));
            } else {
                stringBuilder.append(e.toString());
            }

            if (propertyList.indexOf(e) != propertyList.size() - 1 || uniqueCols.size() > 0) {
                stringBuilder.append(",");
            }
        });

        if (uniqueCols.size() > 0) {
            stringBuilder.append("CONSTRAINT unique_constraints UNIQUE (");
        }

        uniqueCols.stream().forEach(e -> {
            stringBuilder.append(e);
            if (uniqueCols.indexOf(e) != uniqueCols.size() - 1) {
                stringBuilder.append(",");
            }
        });
        if (uniqueCols.size() > 0) {
            stringBuilder.append(")");
        }

        return stringBuilder.toString();
    }
}
