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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.model.Model;
import net.bdavies.db.model.ModelUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Repository for a {@link Model} providing basic CRUD Operation functions
 * <p>
 * Used to build queries and execute them
 *
 * @param <T> Type of model this repository is intended for
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
public class Repository<T extends Model> implements IRepository<Set<T>, Optional<T>, T, T>
{
    private final String table;
    private final Class<T> modelClass;
    private final DatabaseManager manager;

    protected Repository(Class<T> modelClass, DatabaseManager manager)
    {
        this.modelClass = modelClass;
        this.table = ModelUtils.getTableName(modelClass);
        this.manager = manager;
    }

    public Set<T> getAll()
    {
        return manager.createQueryBuilder(table).get(modelClass);
    }

    public Optional<T> findFirst(Consumer<ModelWhereQueryBuilder<T>> builder)
    {
        ModelWhereQueryBuilder<T> queryBuilder =
                new ModelWhereQueryBuilder<>(modelClass, manager.createQueryBuilder(table), manager);
        builder.accept(queryBuilder);
        return Optional.ofNullable(queryBuilder.getObject().first(modelClass));
    }

    public Optional<T> findLast(Consumer<ModelWhereQueryBuilder<T>> builder)
    {
        ModelWhereQueryBuilder<T> queryBuilder =
                new ModelWhereQueryBuilder<>(modelClass, manager.createQueryBuilder(table), manager);
        builder.accept(queryBuilder);
        return Optional.ofNullable(queryBuilder.getObject().last(modelClass));
    }

    public Set<T> find(Consumer<ModelWhereQueryBuilder<T>> builder)
    {
        ModelWhereQueryBuilder<T> queryBuilder =
                new ModelWhereQueryBuilder<>(modelClass, manager.createQueryBuilder(table), manager);
        builder.accept(queryBuilder);
        return queryBuilder.getObject().get(modelClass);
    }

    @SneakyThrows
    public T create(Map<String, Object> values) {
        Constructor<? extends T> modelConstructor = modelClass.getDeclaredConstructor();
        T model = modelConstructor.newInstance();
        model.init(manager, values);
        return model;
    }

    public T create() {
        return create(new HashMap<>());
    }

    public T createAndPersist(Map<String, Object> values) {
        T model = create(values);
        model.save();
        return model;
    }

    public boolean delete(Consumer<ModelWhereQueryBuilder<T>> builder)
    {
        ModelWhereQueryBuilder<T> queryBuilder =
                new ModelWhereQueryBuilder<>(modelClass, manager.createQueryBuilder(table), manager);
        builder.accept(queryBuilder);
        try
        {
            queryBuilder.getObject().get(modelClass).forEach(Model::delete);
        } catch (Exception e) {
            log.error("Error when deleting models", e);
            return false;
        }
        return true;
    }
}
