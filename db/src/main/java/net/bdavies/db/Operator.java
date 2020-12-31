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

package net.bdavies.db;

import lombok.Getter;

/**
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v"></a>
 */
@Getter
public enum Operator {
    /**
     * Less Than e.g. <
     */
    LT("<"),
    /**
     * Less than equal e.g. <=
     */
    LTE("<="),
    /**
     * Greater Than e.g. >
     */
    GT(">"),
    /**
     * Greater Than e.g. >=
     */
    GTE(">="),
    /**
     * Not Equal e.g. !=
     */
    NE("!="),
    /**
     * Equal e.g. =
     */
    EQ("="),
    /**
     * Like value e.g. LIKE %
     * NOTE: for mongoDB you pass a regex string or a keyword
     */
    LIKE("LIKE"),
    /**
     * In Values e.g. IN (val1,val2,val3)
     * NOTE: Mongodb you do not need the () but in SQL you do
     */
    IN("IN"),
    /**
     * Not In Values e.g. NOT IN (val1,val2,val3)
     * NOTE: Mongodb you do not need the () but in SQL you do
     */
    NIN("NOT IN"),
    /**
     * Not Null e.g. NOT NULL
     * Note: for mongodb this checks if the column exists in the document
     */
    NN("NOT NULL"),
    /**
     * No Op used for groups and other things say AND (`yesEnum` = 'yes')
     */
    NONE("");

    private final String operatorString;

    Operator(String s) {
        operatorString = s;
    }
}
