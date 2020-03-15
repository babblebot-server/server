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
