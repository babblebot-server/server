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

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.config.IDatabaseConfig;
import net.bdavies.db.dialect.DBType;
import net.bdavies.db.dialect.connection.IConnection;
import net.bdavies.db.model.Model;
import net.bdavies.db.obj.IQueryObject;

import java.lang.reflect.Constructor;

/**
 * Manager for a database connection and handling creating queries
 * <p>
 * Used by the application and plugin repositories for doing CRUD operations
 * <p>
 * {@code DatabaseManager manager = application.get(DatabaseManager.class); }
 * <p>
 * {@code Repository<User> repo = manager.getRepository(User.class); }
 * <p>
 * {@code ReactiveRepository<User> repo = manager.getReactiveRepository(User.class); }
 * <p>
 * For classes you can use as a field:
 * <p>
 * {@code @InjectRepository(User.class) Repository<User> userRepo; }
 * <p>
 * Or a reactive repository is just as simple as:
 * <p>
 * {@code @InjectRepository(User.class) ReactiveRepository<User> userRepo; }
 *
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Getter
@Slf4j
public class DatabaseManager
{
    @Setter(AccessLevel.PACKAGE)
    private IConnection<?> connection;
    private final Class<? extends IQueryObject> queryObjectClass;
    private final IApplication application;
    private final DBType type;

    @SneakyThrows
    public DatabaseManager(@NonNull IDatabaseConfig config, @NonNull IApplication application)
    {
        this(config, application, DBType.find(config.getType()).getConnectionClass());
    }

    @SneakyThrows
    DatabaseManager(@NonNull IDatabaseConfig config, @NonNull IApplication application,
                           @NonNull Class<? extends IConnection<?>> connectionClass)
    {
        this.application = application;
        this.type = DBType.find(config.getType());
        connection = connectionClass
                .getDeclaredConstructor(DatabaseManager.class).newInstance(this);
        queryObjectClass = type.getQueryObjectClass();
    }

    public <T extends Model> Repository<T> getRepository(Class<T> modelClass)
    {
        return new Repository<>(modelClass, this);
    }

    public <T extends Model> ReactiveRepository<T> getReactiveRepository(Class<T> modelClass)
    {
        return new ReactiveRepository<>(modelClass, this);
    }

    @SneakyThrows
    public IQueryObject createQueryBuilder(@NonNull String table)
    {
        Class<? extends IQueryObject> clazz = type.getQueryObjectClass();
        Constructor<? extends IQueryObject> constructor =
                clazz.getDeclaredConstructor(String.class, IConnection.class, DatabaseManager.class);
        return constructor.newInstance(table, connection, this);
    }

    public void shutdown()
    {
        connection.closeDB();
    }
}
