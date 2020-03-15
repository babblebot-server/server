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
