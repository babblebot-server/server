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

package net.bdavies.db.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.db.DatabaseManager;
import net.bdavies.db.ModelWhereQueryBuilder;
import net.bdavies.db.error.DBError;
import net.bdavies.db.model.fields.ObjectIdProperty;
import net.bdavies.db.model.fields.PrimaryField;
import net.bdavies.db.model.fields.Property;
import net.bdavies.db.model.fields.Protected;
import net.bdavies.db.model.hooks.*;
import net.bdavies.db.model.serialization.*;
import net.bdavies.db.obj.IQueryObject;
import org.apache.commons.lang3.ClassUtils;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@ToString
public abstract class Model
{

    protected List<ModelProperty> properties = new ArrayList<>();
    private SaveType saveType = SaveType.CREATE;
    private Map<String, Model> parents;
    private IApplication application;
    private DatabaseManager manager;
    private IQueryObject queryObject;


    public void init(DatabaseManager manager, Map<String, Object> values, boolean prePopulated)
    {
        this.manager = manager;
        this.application = manager.getApplication();
        if (prePopulated)
        {
            saveType = SaveType.UPDATE;
        }
        parents = new LinkedHashMap<>();
        setupFields();
        reflectFields(values);
        resetQueryObject();
    }


    public void init(DatabaseManager manager, Map<String, Object> values)
    {
        init(manager, values, false);
    }


    public void init(DatabaseManager manager)
    {
        init(manager, new HashMap<>(), false);
    }


    @SneakyThrows
    @SuppressWarnings({"OverlyComplexMethod", "unchecked"})
    private void setupFields()
    {
        for (Field f : this.getClass().getDeclaredFields())
        {
            if (f.isAnnotationPresent(Property.class))
            {
                Property property = f.getAnnotation(Property.class);
                boolean increments = property.increments() || (f.isAnnotationPresent(PrimaryField.class)
                        && f.getAnnotation(PrimaryField.class).increments()) &&
                        (f.getType().equals(int.class) || f.getType().equals(Integer.class));
                Class<?> type = property.type().equals(Object.class) ? f.getType() : property.type();
                DefaultObjectSerializer serializerObj = new DefaultObjectSerializer();
                Class<? extends ISQLObjectSerializer<Model, Object>> serializer;
                Class<? extends ISQLObjectDeserializer<Model, Object>> deSerializer;
                if (!f.isAnnotationPresent(UseSerializationObject.class))
                {
                    serializer = f.isAnnotationPresent(UseSQLObjectSerializer.class)
                            ? (Class<? extends ISQLObjectSerializer<Model, Object>>) f
                            .getAnnotation(UseSQLObjectSerializer.class).value()
                            : serializerObj.getClass();
                    deSerializer = f.isAnnotationPresent(UseSQLObjectDeserializer.class)
                            ? (Class<? extends ISQLObjectDeserializer<Model, Object>>) f
                            .getAnnotation(UseSQLObjectDeserializer.class).value()
                            : serializerObj.getClass();
                } else
                {
                    serializer = (Class<? extends ISQLObjectSerializer<Model, Object>>) f
                            .getAnnotation(UseSerializationObject.class).value();

                    deSerializer = (Class<? extends ISQLObjectDeserializer<Model, Object>>) f
                            .getAnnotation(UseSerializationObject.class).value();
                }
                Class<? extends IPropertyUpdateHook<Model, Object>> onUpdateHook = null;
                if (f.isAnnotationPresent(OnUpdate.class))
                {
                    onUpdateHook = (Class<? extends IPropertyUpdateHook<Model, Object>>)
                            f.getAnnotation(OnUpdate.class)
                                    .value();
                }
                f.setAccessible(true);
                Object v = f.get(this);
                properties.add(new ModelProperty(type, f,
                        property.dbName().isEmpty() ? f.getName() : property.dbName(),
                        f.isAnnotationPresent(Protected.class), f.isAnnotationPresent(PrimaryField.class),
                        increments,
                        serializer, deSerializer, onUpdateHook, null, false, RelationshipType.NONE, false, v != null));
            } else if (f.isAnnotationPresent(ObjectIdProperty.class))
            {
                ObjectIdProperty property = f.getAnnotation(ObjectIdProperty.class);
                f.setAccessible(true);
                boolean deserialize = f.getType().equals(ObjectId.class);
                ISQLObjectDeserializer<Model, ?> objectIdDeserializer = new ObjectIdDeserializer();
                Class<? extends ISQLObjectDeserializer<Model, Object>> objectDeserializer = (Class<?
                        extends ISQLObjectDeserializer<Model, Object>>) (objectIdDeserializer)
                        .getClass();
                Object v = f.get(this);
                properties.add(new ModelProperty(f.getType(), f, property.value(),
                        f.isAnnotationPresent(Protected.class), false,
                        false, null, deserialize ? objectDeserializer : null, null, null, false,
                        RelationshipType.NONE, true, v != null));
            }
        }
    }

    private List<ModelProperty> getAllPrimaryKeys()
    {
        return properties.stream().filter(ModelProperty::isPrimary).collect(Collectors.toList());
    }

    private void resetQueryObject()
    {
        queryObject = manager.createQueryBuilder(ModelUtils.getTableName(this.getClass()));

        getAllPrimaryKeys().forEach(p -> queryObject.where(p.getName(), getPropertyFieldValue(p)));
        properties.stream().filter(p -> !p.isIncrements()).forEach(p -> queryObject.columns(p.getName()));
    }

    private List<ModelProperty> getAllIncrementingProperties()
    {
        return properties.stream().filter(ModelProperty::isIncrements).collect(Collectors.toList());
    }

    private List<ModelProperty> getAllObjectIds()
    {
        return properties.stream().filter(ModelProperty::isObjectId).collect(Collectors.toList());
    }

    private List<ModelProperty> getAllPropertiesWithOnUpdate()
    {
        return properties.stream().filter(mp -> mp.getOnUpdate() != null).collect(Collectors.toList());
    }

    private Map<String, String> modelToValues(boolean includeAll)
    {
        Map<String, String> values = new LinkedHashMap<>();
        if (!includeAll)
        {
            properties.stream().filter(p -> !p.isIncrements())
                    .filter(p -> saveType != SaveType.UPDATE || p.getPreviousValue() == null
                            || !p.getPreviousValue()
                            .equals(getPropertyFieldValue(p)))
                    .filter(p -> !p.isRelational())
                    .filter(p -> !p.isObjectId())
                    .forEach(p -> values.put(p.getName(), getPropertyFieldValue(p)));
        } else
        {
            properties.forEach(p -> values.put(p.getName(), getPropertyFieldValue(p)));
        }
        return values;
    }


    public String serializeObjectByFieldName(Object data, String fieldName, IApplication application)
    {
        ModelProperty property = properties.stream().filter(p -> p.getField().getName().equals(fieldName))
                .findFirst().orElse(null);
        if (property == null) {
            return String.valueOf(data);
        }
        return application.get(property.getSerializer()).serialize(this, data, property);
    }


    private String serializeObject(Object data, ModelProperty property, IApplication application)
    {
        return application.get(property.getSerializer()).serialize(this, data, property);
    }

    private Object deSerializeObject(String data, ModelProperty property, IApplication application)
    {
        return application.get(property.getDeSerializer()).deserialize(this, data, property);
    }

    @SneakyThrows
    private String getPropertyFieldValue(ModelProperty property)
    {
        if (property.getSerializer() == null)
        {
            return String.valueOf(property.getField().get(this));
        }
        return serializeObject(property.getField().get(this), property, application);
    }

    /**
     * This will set the fields based on a Map of values and a application instance
     *
     * @param values the values you wish to set
     */
    void reflectFields(Map<String, Object> values)
    {
        // noinspection OverlyLongLambda
        properties.forEach(p -> {
            Object value = null;
            try
            {
                value = p.getField().get(this);
                if (values.containsKey(p.getName()))
                {
                    if (p.isIncrements() && saveType == SaveType.CREATE)
                    {
                        log.warn("Setting field: {}, which increments automatically, this is not advised!",
                                p.getName());
                    }
                    Object v = values.get(p.getName());
                    value = (ClassUtils.isPrimitiveOrWrapper(v.getClass()) || v instanceof String)
                            ? deSerializeObject(String.valueOf(values.get(p.getName())),
                            p, application) : v;
                }


                try
                {
                    p.getField().setAccessible(true);
                    p.getField().set(this, value);
                    p.setPreviousValue(getPropertyFieldValue(p));
                }
                catch (Exception e)
                {
                    log.error("Cannot deserialize field: {} using deserializer: {}", p.getField().getName(),
                            p.getDeSerializer().getSimpleName(), e);
                }
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        });

    }


    public String toJson(boolean ignoreDefaults, boolean includeProtected)
    {
        Gson gson = new GsonBuilder().create();
        Map<String, String> vals = new LinkedHashMap<>();
            properties.stream()
                    .filter(p -> includeProtected || !p.isProtected())
                    .filter(p -> !ignoreDefaults || !p.isHasDefaultValue())
                    .forEach(p -> vals.put(p.getName(), getPropertyFieldValue(p)));
        return gson.toJson(vals);
    }

    public String toJson()
    {
       return toJson(false, false);
    }

    /**
     * Persist the current state of the model this will write to the DB
     *
     * @return boolean if the persist was successful
     */
    public boolean save()
    {
        if (manager == null)
        {
            throw new DBError("Cannot save without setting up model, please run init");
        }

        if (saveType == SaveType.UPDATE)
        {
            if (this instanceof IUpdateHook)
            {
                ((IUpdateHook) this).onUpdate();
            }


            //noinspection OverlyLongLambda
            getAllPropertiesWithOnUpdate().forEach(p -> {
                try
                {
                    p.getField().setAccessible(true);
                    p.getField().set(this, application.get(p.getOnUpdate()).onUpdate(this));
                }
                catch (Exception e)
                {
                    log.error("Cannot update field: {} using onUpdate: {}", p.getField().getName(),
                            p.getOnUpdate().getSimpleName(), e);
                }
            });

            queryObject.update(modelToValues(false));
            properties.forEach(p -> p.setPreviousValue(getPropertyFieldValue(p)));
            resetQueryObject();

        } else if (saveType == SaveType.CREATE)
        {
            if (this instanceof ICreateHook)
            {
                ((ICreateHook) this).onCreate();
            }

            Map<String, String> values = modelToValues(false);
            queryObject.insert(values);
            saveType = SaveType.UPDATE;
            IQueryObject whereQueryObject = manager
                    .createQueryBuilder(ModelUtils.getTableName(this.getClass()));
            ModelWhereQueryBuilder<? extends Model> whereQueryBuilder =
                    new ModelWhereQueryBuilder<>(this.getClass(), whereQueryObject, manager);
            values.forEach(whereQueryBuilder::where);
            Model model = whereQueryObject.last(this.getClass());
            //noinspection OverlyLongLambda
            getAllIncrementingProperties().forEach(p -> {
                int fromLastModel = 0;
                if (model != null)
                {
                    fromLastModel = Integer.parseInt(model.getPropertyFieldValue(p));
                }
                try
                {
                    p.getField().setAccessible(true);
                    p.getField().set(this, fromLastModel);
                }
                catch (IllegalAccessException e)
                {
                    log.error("Something went wrong when setting property {}", p.getName(), e);
                }
            });
            getAllObjectIds().forEach(p -> {
                String fromLastModel = "";
                if (model != null)
                {
                    if (p.getDeSerializer() == null)
                    {
                        try
                        {
                            p.getField().setAccessible(true);
                            fromLastModel = (String) p.getField().get(model);
                        }
                        catch (IllegalAccessException e)
                        {
                            log.error("Something went wrong", e);
                        }
                    } else
                    {
                        try
                        {
                            p.getField().setAccessible(true);
                            fromLastModel = ((ObjectId) p.getField().get(model)).toHexString();
                        }
                        catch (IllegalAccessException e)
                        {
                            log.error("Something went wrong", e);
                        }
                    }
                }
                try
                {
                    p.getField().setAccessible(true);
                    if (p.getDeSerializer() == null)
                    {
                        p.getField().set(this, fromLastModel);
                    } else
                    {
                        p.getField().set(this, deSerializeObject(fromLastModel, p, application));
                    }
                }
                catch (IllegalAccessException e)
                {
                    log.error("Something went wrong when setting property {}", p.getName(), e);
                }
            });
            properties.forEach(p -> p.setPreviousValue(getPropertyFieldValue(p)));
            resetQueryObject();
        }

        return true;
    }

    /**
     * This will delete and persist the current model
     *
     * @return boolean if the deletion worked
     */
    public boolean delete()
    {
        if (saveType == SaveType.CREATE)
        {
            log.error("Trying to delete model: {}, which does not exist in the db", this.toString());
            return false;
        }

        if (this instanceof IDeleteHook) {
            ((IDeleteHook) this).onDelete();
        }

        if (queryObject.delete())
        {
            this.saveType = SaveType.CREATE;
            resetQueryObject();
            return true;
        }
        return false;
    }

}
