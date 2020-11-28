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

import lombok.Getter;

/**
 * BabbleBot, open-source Discord Bot
 * Author: Ben Davies
 * Class Name: TableBuilder.java
 * Compiled Class Name: TableBuilder.class
 * Date Created: 07/02/2018
 */

public class TableBuilder {
    /**
     * This is the new table's name.
     */
    @Getter
    String tableName;

    /**
     * This will describe the new table so it can be created.
     */
    @Getter
    Blueprint blueprint;

    /**
     * The builder is constructed.
     *
     * @param tableName - The name of the new table.
     * @param blueprint - The table's blueprint of fields.
     */
    public TableBuilder(String tableName, Blueprint blueprint) {
        this.tableName = tableName;
        this.blueprint = blueprint;
    }

    /**
     * This will generate an SQL statement to create the table.
     *
     * @return String
     */
    public String build() {
        blueprint.setup();
        return "CREATE TABLE " + tableName + "(" + blueprint + ")";
    }
}
