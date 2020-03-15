package uk.co.bjdavies.db.Table;

import lombok.Getter;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
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
