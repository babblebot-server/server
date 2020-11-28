package net.bdavies.db.model;

import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.DB;
import net.bdavies.db.model.serialization.*;
import net.bdavies.db.obj.QueryObject;
import uk.co.bjdavies.api.IApplication;
import net.bdavies.db.model.fields.PrimaryField;
import net.bdavies.db.model.fields.Property;
import net.bdavies.db.model.fields.Protected;
import net.bdavies.db.model.hooks.ICreateHook;
import net.bdavies.db.model.hooks.IPropertyUpdateHook;
import net.bdavies.db.model.hooks.IUpdateHook;
import net.bdavies.db.model.hooks.OnUpdate;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@ToString
public class Model {

    protected List<ModelProperty> properties = new ArrayList<>();
    private SaveType saveType = SaveType.CREATE;
    private Map<String, Model> parents;
    private IApplication application;
    private QueryObject queryObject;


    protected Model() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (Arrays.stream(stackTraceElements).noneMatch(
                el -> (el.getMethodName().equals("create") && el.getClassName().contains("Model")))) {
            log.error("Please use DB.create({}.class, Map.of(...))", this.getClass().getSimpleName());
            throw new UnsupportedOperationException("Please do not use the model constructor.., Use DB.create("
                    + getClass().getSimpleName() + ".class, Map.of(...))");
        }
    }

    @SuppressWarnings({"unused", "unchecked"})
    private static <T extends Model> T create(IApplication application, Class<T> clazz, Map<String, String> values, boolean prePopulated) {
        if (application == null) {
            log.error("Please use DB.create({}.class, Map.of(...))", clazz.getSimpleName());
        }

        try {
            assert application != null;
            Model obj = application.get(clazz);
            if (prePopulated) {
                obj.saveType = SaveType.UPDATE;
            }
            obj.parents = new HashMap<>();
            obj.application = application;
            obj.setupFields();
            obj.reflectFields(values);
            obj.resetQueryObject();

            //noinspection CastCanBeRemovedNarrowingVariableType
            return (T) obj;
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This will return all records in the DB for this model
     *
     * @param <T> The model type to return
     * @return {@link Set} of all models for that entity
     * @throws ClassCastException this will be the cause of specifying a different type to the type of the model you run find on
     */
    public static <T extends Model> Set<T> all() {
        return new ModelQueryBuilder<T>(getModelClass()).get(getModelClass());
    }

    /**
     * Return a filtered set of models
     *
     * @param builder The query builder to filter against
     * @param <T>     Model type to return
     * @return {@link Set} of all models
     * @throws ClassCastException this will be the cause of specifying a different type to the type of the model you run find on
     */
    public static <T extends Model> Set<T> find(Consumer<IModelQueryBuilder<T>> builder) {
        IModelQueryBuilder<T> modelQueryBuilder = new ModelQueryBuilder<>(getModelClass());
        builder.accept(modelQueryBuilder);
        return modelQueryBuilder.get(getModelClass());
    }

    /**
     * Return a filtered model the first one found
     *
     * @param builder The query builder to filter against
     * @param <T>     Model type to return
     * @return a filtered model
     * @throws ClassCastException this will be the cause of specifying a different type to the type of the model you run find on
     */
    public static <T extends Model> Optional<T> findOne(Consumer<IModelQueryBuilder<T>> builder) {
        IModelQueryBuilder<T> modelQueryBuilder = new ModelQueryBuilder<>(getModelClass());
        builder.accept(modelQueryBuilder);
        return Optional.of(modelQueryBuilder.first(getModelClass()));
    }

    /**
     * Return a filtered model the last one found
     *
     * @param builder The query builder to filter against
     * @param <T>     Model type to return
     * @return a filtered model
     * @throws ClassCastException this will be the cause of specifying a different type to the type of the model you run find on
     */
    public static <T extends Model> Optional<T> findLast(Consumer<IModelQueryBuilder<T>> builder) {
        IModelQueryBuilder<T> modelQueryBuilder = new ModelQueryBuilder<>(getModelClass());
        builder.accept(modelQueryBuilder);
        return Optional.of(modelQueryBuilder.last(getModelClass()));
    }


    /**
     * Delete all Models of this type
     *
     * @return boolean if operation succeeded
     */
    public static boolean deleteAll() {
        return new ModelQueryBuilder<>(getModelClass()).delete();
    }

    private static <T extends Model> Class<T> getModelClass() {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    @SuppressWarnings({"OverlyComplexMethod", "unchecked"})
    private void setupFields() {
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Property.class)) {
                Property property = f.getAnnotation(Property.class);
                boolean increments = property.increments() || (f.isAnnotationPresent(PrimaryField.class)
                        && f.getAnnotation(PrimaryField.class).increments()) &&
                        (f.getType().equals(int.class) || f.getType().equals(Integer.class));
                Class<?> type = property.type().equals(Object.class) ? f.getType() : property.type();
                DefaultObjectSerializer serializerObj = new DefaultObjectSerializer();
                Class<? extends ISQLObjectSerializer<Model, Object>> serializer;
                Class<? extends ISQLObjectDeserializer<Model, Object>> deSerializer;
                if (!f.isAnnotationPresent(UseSerializationObject.class)) {
                    serializer = f.isAnnotationPresent(UseSQLObjectSerializer.class)
                            ? (Class<? extends ISQLObjectSerializer<Model, Object>>) f
                            .getAnnotation(UseSQLObjectSerializer.class).value()
                            : serializerObj.getClass();
                    deSerializer = f.isAnnotationPresent(UseSQLObjectDeserializer.class)
                            ? (Class<? extends ISQLObjectDeserializer<Model, Object>>) f
                            .getAnnotation(UseSQLObjectDeserializer.class).value()
                            : serializerObj.getClass();
                } else {
                    serializer = (Class<? extends ISQLObjectSerializer<Model, Object>>) f
                            .getAnnotation(UseSerializationObject.class).value();

                    deSerializer = (Class<? extends ISQLObjectDeserializer<Model, Object>>) f
                            .getAnnotation(UseSerializationObject.class).value();
                }
                Class<? extends IPropertyUpdateHook<Model, Object>> onUpdateHook = null;
                if (f.isAnnotationPresent(OnUpdate.class)) {
                    onUpdateHook = (Class<? extends IPropertyUpdateHook<Model, Object>>)
                            f.getAnnotation(OnUpdate.class)
                                    .value();
                }
                f.setAccessible(true);
                properties.add(new ModelProperty(type, f, property.dbName().isEmpty() ? f.getName() : property.dbName(),
                        f.isAnnotationPresent(Protected.class), f.isAnnotationPresent(PrimaryField.class), increments,
                        serializer, deSerializer, onUpdateHook, null, false, Relationship.NONE));
            }
        }
    }

    private List<ModelProperty> getAllPrimaryKeys() {
        return properties.stream().filter(ModelProperty::isPrimary).collect(Collectors.toList());
    }

    private void resetQueryObject() {
        queryObject = DB.buildObject(QueryObject.class, ModelUtils.getTableName(this.getClass()));

        getAllPrimaryKeys().forEach(p -> queryObject.where(p.getName(), getPropertyFieldValue(p)));
        properties.stream().filter(p -> !p.isIncrements()).forEach(p -> queryObject.columns(p.getName()));
    }

    private List<ModelProperty> getAllIncrementingProperties() {
        return properties.stream().filter(ModelProperty::isIncrements).collect(Collectors.toList());
    }

    private List<ModelProperty> getAllPropertiesWithOnUpdate() {
        return properties.stream().filter(mp -> mp.getOnUpdate() != null).collect(Collectors.toList());
    }

    private Map<String, String> modelToValues() {
        Map<String, String> values = new LinkedHashMap<>();
        properties.stream().filter(p -> !p.isIncrements()).filter(p -> p.getPreviousValue() == null
                || !p.getPreviousValue()
                .equals(getPropertyFieldValue(p)))
                .filter(p -> !p.isRelational())
                .forEach(p -> values.put(p.getName(), getPropertyFieldValue(p)));
        return values;
    }

    public String serializeObject(Object data, ModelProperty property, IApplication application) {
        return application.get(property.getSerializer()).serialize(this, data, property);
    }

    private Object deSerializeObject(String data, ModelProperty property, IApplication application) {
        return application.get(property.getDeSerializer()).deserialize(this, data, property);
    }

    @SneakyThrows
    private String getPropertyFieldValue(ModelProperty property) {
        return serializeObject(property.getField().get(this), property, application);
    }

    /**
     * This will set the fields based on a Map of values and a application instance
     *
     * @param values the values you wish to set
     */
    public void reflectFields(Map<String, String> values) {
        // noinspection OverlyLongLambda
        properties.forEach(p -> {
            Object value = null;
            try {
                value = p.getField().get(this);
                if (values.containsKey(p.getName())) {
                    if (p.isIncrements() && saveType == SaveType.CREATE) {
                        log.warn("Setting field: {}, which increments automatically, this is not advised!", p.getName());
                    }
                    value = deSerializeObject(values.get(p.getName()), p, application);
                }


                try {
                    p.getField().setAccessible(true);
                    p.getField().set(this, value);
                    p.setPreviousValue(getPropertyFieldValue(p));
                } catch (Exception e) {
                    log.error("Cannot deserialize field: {} using deserializer: {}", p.getField().getName(),
                            p.getDeSerializer().getSimpleName(), e);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Persist the current state of the model this will write to the DB
     *
     * @return boolean if the persist was successful
     */
    public boolean save() {
        if (saveType == SaveType.UPDATE) {
            if (this instanceof IUpdateHook) {
                ((IUpdateHook) this).onUpdate();
            }


            //noinspection OverlyLongLambda
            getAllPropertiesWithOnUpdate().forEach(p -> {
                try {
                    p.getField().setAccessible(true);
                    p.getField().set(this, application.get(p.getOnUpdate()).onUpdate(this));
                } catch (Exception e) {
                    log.error("Cannot update field: {} using onUpdate: {}", p.getField().getName(),
                            p.getOnUpdate().getSimpleName(), e);
                }
            });

            queryObject.update(modelToValues());
            properties.forEach(p -> p.setPreviousValue(getPropertyFieldValue(p)));
            resetQueryObject();

        } else if (saveType == SaveType.CREATE) {
            if (this instanceof ICreateHook) {
                ((ICreateHook) this).onCreate();
            }
            IModelQueryBuilder<Model> modelQueryBuilder = new ModelQueryBuilder<>(getModelClass());
            Model model = modelQueryBuilder.last(getModelClass());
            queryObject.insert(modelToValues());
            saveType = SaveType.UPDATE;
            //noinspection OverlyLongLambda
            getAllIncrementingProperties().forEach(p -> {
                int fromLastModel = 0;
                if (model != null) {
                    fromLastModel = Integer.parseInt(model.getPropertyFieldValue(p));
                }
                try {
                    p.getField().setAccessible(true);
                    p.getField().set(this, fromLastModel + 1);
                } catch (IllegalAccessException e) {
                    log.error("Something went wrong when setting property {}", p.getName(), e);
                }
            });
            properties.forEach(p -> p.setPreviousValue(getPropertyFieldValue(p)));
        }

        return true;
    }

    /**
     * This will delete and persist the current model
     *
     * @return boolean if the deletion worked
     */
    public boolean delete() {
        if (saveType == SaveType.CREATE) {
            log.error("Trying to delete model: {}, which does not exist in the db", this.toString());
            return false;
        }

        if (queryObject.delete()) {
            this.saveType = SaveType.CREATE;
            resetQueryObject();
            return true;
        }
        return false;
    }

}
