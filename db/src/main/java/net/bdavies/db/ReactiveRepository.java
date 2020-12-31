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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Reactive Repository for a {@link Model} providing basic CRUD Operation functions using project reactor
 * methods
 * <p>
 * Used to build queries and execute them
 *
 * @param <T> Type of model this repository is intended for
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
public class ReactiveRepository<T extends Model> implements IRepository<Flux<T>, Mono<T>, Mono<T>, T>
{
    private final String table;
    private final Class<T> modelClass;
    private final DatabaseManager manager;

    protected ReactiveRepository(Class<T> modelClass, DatabaseManager manager)
    {
        this.modelClass = modelClass;
        this.table = ModelUtils.getTableName(modelClass);
        this.manager = manager;
    }

    public Flux<T> getAll()
    {
        return Flux.fromStream(manager.createQueryBuilder(table).get(modelClass).stream());
    }

    public Mono<T> findFirst(Consumer<ModelWhereQueryBuilder<T>> builder)
    {
        ModelWhereQueryBuilder<T> queryBuilder =
                new ModelWhereQueryBuilder<>(modelClass, manager.createQueryBuilder(table), manager);
        builder.accept(queryBuilder);
        return Mono.just(queryBuilder.getObject().first(modelClass));
    }

    public Mono<T> findLast(Consumer<ModelWhereQueryBuilder<T>> builder)
    {
        ModelWhereQueryBuilder<T> queryBuilder =
                new ModelWhereQueryBuilder<>(modelClass, manager.createQueryBuilder(table), manager);
        builder.accept(queryBuilder);
        return Mono.just(queryBuilder.getObject().last(modelClass));
    }

    public Flux<T> find(Consumer<ModelWhereQueryBuilder<T>> builder)
    {
        ModelWhereQueryBuilder<T> queryBuilder =
                new ModelWhereQueryBuilder<>(modelClass, manager.createQueryBuilder(table), manager);
        builder.accept(queryBuilder);
        return Flux.fromStream(queryBuilder.getObject().get(modelClass).stream());
    }


    @SneakyThrows
    public Mono<T> create(Map<String, Object> values) {
        Constructor<? extends T> modelConstructor = modelClass.getDeclaredConstructor();
        T model = modelConstructor.newInstance();
        model.init(manager, values);
        return Mono.just(model);
    }

    public Mono<T> create() {
        return create(new HashMap<>());
    }

    public Mono<T> createAndPersist(Map<String, Object> values) {
        return create(values).doOnNext(Model::save);
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
