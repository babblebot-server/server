package uk.co.bjdavies.db.Table;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: IntegerProperty.java
 * Compiled Class Name: IntegerProperty.class
 * Date Created: 08/02/2018
 */

public class IntegerProperty extends Property {
    /**
     * This determines if the integer auto increments.
     */
    private final boolean increments;

    /**
     * This constructs a IntegerProperty.
     *
     * @param fieldName  - The name of the field.
     * @param increments - does the integer auto increment?
     */
    public IntegerProperty(String fieldName, boolean increments) {
        super(fieldName);
        this.increments = increments;
    }


    /**
     * This will return the description of the Integer.
     *
     * @return String
     */
    @Override
    public String toString() {
        return fieldName + " " + "INTEGER " + ((increments) ? "AUTOINCREMENT " : "") + getStringEnding();
    }
}
