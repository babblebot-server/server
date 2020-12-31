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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.model.Model;
import net.bdavies.db.model.ModelUtils;
import net.bdavies.db.model.serialization.util.Utilities;
import net.bdavies.db.obj.IGroupableWhereBuilder;
import net.bdavies.db.obj.IQueryObject;
import net.bdavies.db.obj.IWhereBuilder;
import net.bdavies.db.query.QueryLink;
import reactor.core.publisher.Mono;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class ModelWhereQueryBuilder<T extends Model> implements IWhereBuilder,
        IGroupableWhereBuilder<ModelWhereQueryBuilder<T>>
{
    @Getter(AccessLevel.PACKAGE)
    private final IQueryObject object;
    private final Class<T> modelClass;
    private final DatabaseManager manager;

    public ModelWhereQueryBuilder(Class<T> modelClazz, IQueryObject object, DatabaseManager manager)
    {
        this.object = object;
        this.modelClass = modelClazz;
        this.manager = manager;
        object.orderBy(ModelUtils.getPrimaryKey(modelClazz));
    }

    @Override
    public ModelWhereQueryBuilder<T> where(String col, String val)
    {
        object.where(col, val);
        return this;
    }

    @Override
    public ModelWhereQueryBuilder<T> and(String col, String val)
    {
        object.and(col, val);
        return this;
    }

    @Override
    public ModelWhereQueryBuilder<T> or(String col, String val)
    {
        object.or(col, val);
        return this;
    }

    @Override
    public ModelWhereQueryBuilder<T> where(String col, Operator operator, String val)
    {
        object.where(col, operator, val);
        return this;
    }

    @Override
    public ModelWhereQueryBuilder<T> and(String col, Operator operator, String val)
    {
        object.and(col, operator, val);
        return this;
    }

    @Override
    public ModelWhereQueryBuilder<T> or(String col, Operator operator, String val)
    {
        object.or(col, operator, val);
        return this;
    }


    @Override
    public ModelWhereQueryBuilder<T> group(IQueryObject object, QueryLink link)
    {
        this.object.group(object, link);
        return this;
    }

    @Override
    public ModelWhereQueryBuilder<T> group(IQueryObject object)
    {
        this.object.group(object);
        return this;
    }

    @Override
    public ModelWhereQueryBuilder<T> group(Consumer<ModelWhereQueryBuilder<T>> builder, QueryLink link)
    {
        ModelWhereQueryBuilder<T> queryBuilder = new ModelWhereQueryBuilder<>(modelClass,
                manager.createQueryBuilder(ModelUtils.getTableName(modelClass)), manager);

        builder.accept(queryBuilder);
        group(queryBuilder.getObject(), link);
        return this;
    }

    @Override
    public ModelWhereQueryBuilder<T> group(
            Consumer<ModelWhereQueryBuilder<T>> builder)
    {
        return group(builder, QueryLink.AND);
    }


    public ModelWhereQueryBuilder<T> filter(Predicate<T> predicate)
    {
        object.addModelPredicate(predicate);
        return this;
    }


    @SneakyThrows
    public <R> ModelWhereQueryBuilder<T> where(ModelFunction<T, R> fnc, Operator operator, R value)
    {
        T model = modelClass.getDeclaredConstructor().newInstance();
        model.init(manager);
        String fieldName = getFieldName(modelClass, fnc.getMethodName());
        Object newValue = value;
        if (newValue instanceof Mono) {
            Mono<?> mono = (Mono<?>) newValue;
            newValue = Utilities.monoBlock(mono);
        }
        object.where(fieldName, operator,
                model.serializeObjectByFieldName(newValue, fieldName, manager.getApplication()));
        return this;
    }


    @SneakyThrows
    public <R> ModelWhereQueryBuilder<T> and(ModelFunction<T, R> fnc, Operator operator, R value)
    {
        T model = modelClass.getDeclaredConstructor().newInstance();
        model.init(manager);
        String fieldName = getFieldName(modelClass, fnc.getMethodName());
        Object newValue = value;
        if (newValue instanceof Mono) {
            Mono<?> mono = (Mono<?>) newValue;
            newValue = Utilities.monoBlock(mono);
        }
        object.and(fieldName, operator,
                model.serializeObjectByFieldName(newValue, fieldName, manager.getApplication()));
        return this;
    }


    @SneakyThrows
    public <R> ModelWhereQueryBuilder<T> or(ModelFunction<T, R> fnc, Operator operator, R value)
    {
        T model = modelClass.getDeclaredConstructor().newInstance();
        model.init(manager);
        String fieldName = getFieldName(modelClass, fnc.getMethodName());
        Object newValue = value;
        if (newValue instanceof Mono) {
            Mono<?> mono = (Mono<?>) newValue;
            newValue = Utilities.monoBlock(mono);
        }
        object.or(fieldName, operator,
                model.serializeObjectByFieldName(newValue, fieldName, manager.getApplication()));
        return this;
    }


    public <R> ModelWhereQueryBuilder<T> where(ModelFunction<T, R> fnc, R value)
    {
        return where(fnc, Operator.EQ, value);
    }


    public <R> ModelWhereQueryBuilder<T> and(ModelFunction<T, R> fnc, R value)
    {
        return and(fnc, Operator.EQ, value);
    }


    public <R> ModelWhereQueryBuilder<T> or(ModelFunction<T, R> fnc, R value)
    {
        return or(fnc, Operator.EQ, value);
    }


    public static String getFieldName(Class<?> modelClass, String methodName)
    {
        try
        {
            Method method = modelClass.getDeclaredMethod(methodName);
            BeanInfo info = Introspector.getBeanInfo(modelClass);
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : props)
            {
                if (method.equals(pd.getWriteMethod()) || method.equals(pd.getReadMethod()))
                {
                    log.info(pd.getDisplayName());
                    return pd.getName();
                }
            }
        }
        catch (IntrospectionException | NoSuchMethodException e)
        {
            log.error("An Error occurred when trying to get method field name", e);
        }

        return "";
    }

}
