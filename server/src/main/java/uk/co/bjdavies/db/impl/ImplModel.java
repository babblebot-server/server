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

package uk.co.bjdavies.db.impl;

import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.db.Comparator;
import uk.co.bjdavies.api.db.*;
import uk.co.bjdavies.api.db.fields.IntField;
import uk.co.bjdavies.api.db.fields.PrimaryField;
import uk.co.bjdavies.api.db.fields.Protected;
import uk.co.bjdavies.api.db.fields.StringField;
import uk.co.bjdavies.api.db.methods.BelongsTo;
import uk.co.bjdavies.api.db.methods.HasMany;
import uk.co.bjdavies.db.DB;
import uk.co.bjdavies.db.DBRecord;
import uk.co.bjdavies.db.ModelUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
//TODO: Refactor Class follow S.O.L.I.D principles
@Slf4j
public class ImplModel extends Model {

    private final DBRecord record;
    protected String primaryKey;
    protected String tableName = ModelUtils.getTableName(getClass());
    protected String[] protected_fields;
    private boolean increments = true;
    private String saveType = "create";
    private Map<String, Model> parents;

    protected ImplModel() {
        record = new DBRecord(new HashMap<>());
        parents = new HashMap<>();
        setupFields();
        ModelUtils.createTable(getClass());
    }

    public static <T extends Model> List<T> all() {
        return ModelUtils.all(getModelClass());
    }

    public static <T extends Model> IModelBuilder<T> where(String key, Object value) {
        return ModelUtils.where(getModelClass(), key, value);
    }

    public static <T extends Model> IModelBuilder<T> where(String key, Comparator comparator, Object value) {
        return ModelUtils.where(getModelClass(), key, comparator, value);
    }

    public static <T extends Model> IModelBuilder<T> where(WhereStatement statement) {
        return ModelUtils.where(getModelClass(), statement);
    }

    public static <T extends Model> Optional<T> find(int id) {
        return ModelUtils.find(getModelClass(), id);
    }

    public static <T extends Model> T findOrFail(int id) throws NullPointerException {
        return ModelUtils.findOrFail(getModelClass(), id);
    }

    private static <T extends Model> Class<T> getModelClass() {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    private void setupFields() {
        this.primaryKey = Arrays.stream(this.getClass().getDeclaredFields()).filter(f ->
                f.isAnnotationPresent(PrimaryField.class)).map(Field::getName).findAny().orElse("id");
        this.increments = Arrays.stream(this.getClass().getDeclaredFields()).anyMatch(f ->
                f.isAnnotationPresent(PrimaryField.class) && f.isAnnotationPresent(IntField.class));
        this.protected_fields = Arrays.stream(this.getClass().getDeclaredFields()).filter(f ->
                f.isAnnotationPresent(Protected.class)).map(Field::getName).toArray(String[]::new);
    }

    @Override
    public Object get(String key) {
        return record.get(key);
    }

    @Override
    public Map<String, Object> getData() {
        return record.getData();
    }

    @Override
    public void setData(Map<String, Object> data) {
        record.setData(data);
        this.setFields();
    }

    @Override
    public String getString(String key) {
        return record.getString(key);
    }

    @Override
    public int getInt(String key) {
        return record.getInt(key);
    }

    @Override
    public float getFloat(String key) {
        return record.getFloat(key);
    }

    @Override
    public String toJsonString(String... keysToHide) {
        return record.toJsonString(keysToHide);
    }

    protected void setFields() {
        this.saveType = "update";
        Arrays.stream(this.getClass().getDeclaredFields()).forEach(f -> {
            if (f.isAnnotationPresent(IntField.class)) {
                f.setAccessible(true);
                try {
                    f.setInt(this, this.getInt(f.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (f.isAnnotationPresent(StringField.class)) {
                f.setAccessible(true);
                try {
                    f.set(this, this.getString(f.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        // Setup relations
        Arrays.stream(this.getClass().getDeclaredFields()).forEach(f -> {
            if (f.isAnnotationPresent(BelongsTo.class)) {
                try {
                    f.setAccessible(true);
                    //noinspection unchecked
                    Class<Model> modelType = (Class<Model>) f.getType();
                    if (this.parents.containsKey(ModelUtils.getTableName(modelType))) {
                        f.set(this, parents.get(ModelUtils.getTableName(modelType)));
                    } else {
                        BelongsTo belongsTo = f.getAnnotation(BelongsTo.class);
                        String newForeignKey = belongsTo.foreignKey().equals("") ?
                                f.getType().getSimpleName().toLowerCase() + "Id" : belongsTo.foreignKey();
                        String newLocalKey = belongsTo.localKey().equals("") ? this.primaryKey : belongsTo.localKey();
                        //noinspection unchecked
                        f.set(this, this.belongsTo((Class<? extends Model>) f.getType(), newForeignKey, newLocalKey));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (f.isAnnotationPresent(HasMany.class)) {
                try {
                    f.setAccessible(true);
                    //noinspection unchecked
                    Class<Model> modelType = (Class<Model>) f.getType();
                    if (this.parents.containsKey(ModelUtils.getTableName(modelType))) {
                        f.set(this, parents.get(ModelUtils.getTableName(modelType)));
                    } else {
                        HasMany belongsTo = f.getAnnotation(HasMany.class);
                        ParameterizedType pType = (ParameterizedType) f.getGenericType();
                        Type type = pType.getActualTypeArguments()[0];
                        String newForeignKey = belongsTo.foreignKey().equals("") ?
                                getClass().getSimpleName().toLowerCase() + "Id" : belongsTo.foreignKey();
                        String newLocalKey = belongsTo.localKey().equals("") ? this.primaryKey : belongsTo.localKey();
                        //noinspection unchecked
                        f.set(this, this.hasMany((Class<? extends Model>) type, newForeignKey, newLocalKey));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void save() {
        Map<String, Object> data = new HashMap<>();
        Arrays.stream(this.getClass().getDeclaredFields()).forEach(f -> {
            if (f.isAnnotationPresent(IntField.class)) {
                try {
                    f.setAccessible(true);
                    data.put(f.getName(), f.getInt(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (f.isAnnotationPresent(StringField.class)) {
                try {
                    f.setAccessible(true);
                    data.put(f.getName(), f.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        switch (saveType) {
            case "create":
                try {
                    createModel(data);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case "update":
                updateModel(data);
                break;
        }

        this.setData(data);

    }

    public boolean delete() {
        return DB.getConnection().createCommandBuilder(this.tableName).where(this.primaryKey, this.get(this.primaryKey))
                .delete();
    }

    private void updateModel(Map<String, Object> data) {
        int id = -1;
        if (increments) {
            id = (int) data.get(this.primaryKey);
            data.remove(this.primaryKey);
        }
        DB.getConnection().createCommandBuilder(this.tableName).where(this.primaryKey, this.get(this.primaryKey))
                .update(data);
        if (increments) {
            data.put(this.primaryKey, id);
        }
    }

    @Override
    protected <E extends Model> List<E> hasMany(Class<E> clazz, String foreignKey, String localKey) {
        IQueryBuilder<IDBRecord> query =
                DB.getConnection().
                        createQueryBuilder(ModelUtils.getTableName(clazz), ModelUtils.getPrimaryKey(clazz));

        return covertListToRelationalModels(clazz, query.where(foreignKey, this.get(localKey)).get());
    }

    private <E extends Model> List<E> covertListToRelationalModels(Class<E> clazz, List<IDBRecord> executeQuery) {
        List<E> models = new ArrayList<>();

        for (IDBRecord t : executeQuery) {
            try {
                parents.put(ModelUtils.getTableName(getClass()), this);
                E model = clazz.getDeclaredConstructor().newInstance();
                model.setParents(parents);
                model.setData(t.getData());
                models.add(model);
            } catch (Exception e) {
                log.error("Unable to map relational model", e);
            }
        }

        return models;
    }

    public void setParents(Map<String, Model> parents) {
        this.parents = parents;
    }

    protected <E extends Model> E belongsTo(Class<E> clazz, String foreignKey, String localKey) {
        IQueryBuilder<IDBRecord> query =
                DB.getConnection().
                        createQueryBuilder(ModelUtils.getTableName(clazz), ModelUtils.getPrimaryKey(clazz));

        E returnType = covertListToRelationalModels(clazz, query.where(localKey, this.get(foreignKey)).get()).get(0);
        if (returnType == null) {
            log.info("BelongsTo Failed, please check your foreign key and or if the other row " +
                    "exists in the relation");
        }
        return returnType;
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
        List<String> tableNames = new ArrayList<>();
        String foreignTableName = clazz.getSimpleName().toLowerCase();
        String primaryTableName = getClass().getSimpleName().toLowerCase();
        tableNames.add(foreignTableName);
        tableNames.add(primaryTableName);
        tableNames.sort(java.util.Comparator.naturalOrder());

        String intermediateTable = String.join("_", tableNames);
        List<IDBRecord> records = DB.getConnection().createQueryBuilder(intermediateTable, "id").where(primaryTableName + "Id",
                this.get(this.primaryKey)).get();

        IModelBuilder<E> model = ModelUtils.where(clazz, "id", records.get(0).get(foreignTableName + "Id"));
        for (int i = 1; i < records.size() - 1; i++) {
            model.or(new WhereStatement("id", records.get(0).get(foreignTableName + "Id")));
        }
        return model.get();
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
        //noinspection unchecked
        return (E) ModelUtils.where(clazz, this.getClass().getSimpleName().toLowerCase() + "Id", this.get("id"))
                .first().orElse(null);
    }

    private void createModel(Map<String, Object> data) throws IllegalAccessException {
        Optional<Field> primaryKeyField = Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> f.getName()
                .equals(primaryKey)).findFirst();

        if (DB.getConnection().createCommandBuilder(this.tableName).insert(data)) {
            int newId = ModelUtils.all(getClass()).size() + 1;
            if (primaryKeyField.isPresent() && increments) {
                Field f = primaryKeyField.get();
                f.setAccessible(true);
                f.set(this, newId);
                data.put(this.primaryKey, newId);
            }
        }
    }

    public String toJson() {
        return this.toJsonString(this.protected_fields);
    }
}
