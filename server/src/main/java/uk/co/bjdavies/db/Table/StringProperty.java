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
 * Class Name: StringProperty.java
 * Compiled Class Name: StringProperty.class
 * Date Created: 08/02/2018
 */

public class StringProperty extends Property {

    /**
     * This determines id the string has a character limit.
     */
    private boolean hasLimit = false;


    /**
     * This determines the limit of characters in the string.
     */
    private int limit = 0;


    /**
     * This is the constructor for a StringProperty.
     *
     * @param fieldName - The name of the field.
     * @param charLimit - THe character limit of the string field.
     */
    public StringProperty(String fieldName, int charLimit) {
        super(fieldName);
        hasLimit = true;
        limit = charLimit;
    }


    /**
     * This is the constructor for a StringProperty.
     *
     * @param fieldName - The name of the field.
     */
    public StringProperty(String fieldName) {
        super(fieldName);
    }


    /**
     * This will return the description of the property.
     *
     * @return String
     */
    @Override
    public String toString() {
        return fieldName + " VARCHAR" + ((hasLimit) ? "(" + limit + ") " : " ") + getStringEnding();
    }
}
