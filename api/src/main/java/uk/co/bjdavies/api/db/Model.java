package uk.co.bjdavies.api.db;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This is the model class which gets converted at runtime by the babblebot-agent.
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public abstract class Model implements IDBRecord {


    /**
     * Construct a Model.
     */
    protected Model() {
    }

    /**
     * This will return a {@link IQueryBuilder} where you can find a model you want or get all of them.
     * {@link IQueryBuilder#get()}
     *
     * @return {@link IQueryBuilder}
     */
    public static <T extends Model> List<T> all() {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    /**
     * This will return a {@link IQueryBuilder} where you can find a model you want by column and value
     *
     * @return {@link IQueryBuilder}
     */
    public static <T extends Model> IModelBuilder<T> where(String key, Object value) {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    public static <T extends Model> IModelBuilder<T> where(String key, Comparator comparator, Object value) {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    public static <T extends Model> IModelBuilder<T> where(WhereStatement statement) {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    /**
     * This will return a {@link Optional<Model>} by the id you specify.
     *
     * @return {@link IQueryBuilder}
     */
    public static <T extends Model> Optional<T> find(int id) {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    /**
     * This will return a {@link Model} by the id you specify or fail.
     *
     * @return {@link IQueryBuilder}
     */
    public static <T extends Model> T findOrFail(int id) throws NullPointerException {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    /**
     * This is internal method that the agent will change so you can run Model.all(), Model.where() etc.
     *
     * @return Class of the model.
     */
    private static <T extends Model> Class<T> getModelClass() {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    /**
     * Set all the field's types for the schema generation etc.
     */
    private void setupFields() {
    }

    /**
     * This will set the fields from the data passed in.
     */
    protected void setFields() {
    }

    /**
     * This will persist data that has been changed by the model.
     * if the model is new then the model will be saved as a new record in the db.
     * and if its not an existing row will will be updated
     */
    public void save() {
    }

    /**
     * This will delete the current model be careful once this is done the data will be deleted from the database and
     * cannot be undone.
     */
    public boolean delete() {
        return false;
    }

    /**
     * To update model.
     */
    private void updateModel() {

    }

    /**
     * Create a model.
     *
     * @throws IllegalAccessException
     */
    private void createModel() throws IllegalAccessException {
    }

    /**
     * This will return a one to many relationship between two relations
     * e.g.
     * <p>
     * Ignore has Many Users
     * user.ignoreId = ignore.id etc.
     *
     * @param clazz - the linking model
     * @return List a list of the relation
     */
    protected <E extends Model> List<E> hasMany(Class<E> clazz, String foreignKey, String localKey) {
        return null;
    }

    /**
     * This will return a one to one relation
     * e.g.
     * <p>
     * User belongsTo a Ignore
     * user.ignoreId = ignore.id etc.
     *
     * @param clazz - the linking model
     * @return List a list of the relation
     */
    protected <E extends Model> E belongsTo(Class<E> clazz, String foreignKey, String localKey) {
        return null;
    }

    /**
     * This will return a many to many between two relations
     * REQUIRES: a intermediate table.
     * e.g.
     * <p>
     * User belongsToMany Ignore
     * IgnoreUser Table
     * ignoreUser.ignoreId = 1
     * ignoreUser.userId = 1
     *
     * @param clazz - the linking model
     * @return List a list of the relation
     */
    protected <E extends Model> List<E> belongsToMany(Class<E> clazz) {
        return null;
    }

    public void setParents(Map<String, Model> parents) {
    }


    /**
     * This will return a one to one relation
     * e.g.
     * <p>
     * User HasOne Ignore
     *
     * @param clazz - the linking model
     * @return List a list of the relation
     */
    protected <E extends Model> E hasOne(Class<E> clazz) {
        return null;
    }

    /**
     * Convert your model to json.
     *
     * @return
     */
    public String toJson() {
        return null;
    }

    /**
     * Refer to {@link IDBRecord}
     *
     * @param key
     * @return
     */
    @Override
    public Object get(String key) {
        return null;
    }

    /**
     * Refer to {@link IDBRecord}
     *
     * @return
     */
    @Override
    public Map<String, Object> getData() {
        return null;
    }

    /**
     * Refer to {@link IDBRecord}
     *
     * @return
     */
    @Override
    public void setData(Map<String, Object> data) {
    }

    /**
     * Refer to {@link IDBRecord}
     *
     * @return
     */
    @Override
    public String getString(String key) {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    /**
     * Refer to {@link IDBRecord}
     *
     * @return
     */
    @Override
    public int getInt(String key) {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    /**
     * Refer to {@link IDBRecord}
     *
     * @return
     */
    @Override
    public float getFloat(String key) {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    /**
     * Refer to {@link IDBRecord}
     *
     * @return
     */
    @Override
    public String toJsonString(String... keysToHide) {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    /**
     * Refer to {@link IDBRecord}
     *
     * @return
     */
    @Override
    public boolean equals(String key, String value) {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }
}
