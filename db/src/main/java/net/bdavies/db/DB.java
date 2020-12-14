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
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.dialect.connection.IConnection;
import net.bdavies.db.dialect.connection.MongoDBConnection;
import net.bdavies.db.dialect.connection.MySQLConnection;
import net.bdavies.db.dialect.obj.MongoDBQueryObject;
import net.bdavies.db.dialect.obj.MySQLQueryObject;
import net.bdavies.db.dialect.obj.SQLiteQueryObject;
import net.bdavies.db.error.DBError;
import net.bdavies.db.model.Model;
import net.bdavies.db.obj.IQueryObject;
import net.bdavies.db.query.QueryBuilder;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.config.IDatabaseConfig;
import net.bdavies.db.dialect.connection.SQLiteConnection;
import net.bdavies.db.obj.QueryObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 3.0.0
 */
@Slf4j
@UtilityClass
public final class DB {
    private static final Map<Class<? extends IQueryObject>, Class<? extends IQueryObject>> objectMap = new HashMap<>();
    private static final String ERROR_MESSAGE = "Trying to use the DB before initialising it first";
    @SuppressWarnings("rawtypes")
    private static IConnection dbConnection = null;

    public static void init(IDatabaseConfig databaseConfig, IApplication application) {
        if (assertInitialisedDB()) {
            switch (databaseConfig.getType().toLowerCase()) {
                case "sqlite":
                    dbConnection = new SQLiteConnection(databaseConfig, application);
                    objectMap.put(QueryObject.class, SQLiteQueryObject.class);
                    break;
                case "mysql":
                    dbConnection = new MySQLConnection(databaseConfig, application);
                    objectMap.put(QueryObject.class, MySQLQueryObject.class);
                    break;
                case "mongodb":
                    dbConnection = new MongoDBConnection(databaseConfig, application);
                    log.warn("MongoDB support is very experimental and will most likely not work as expected please make an issue for any bugs found!");
                    objectMap.put(QueryObject.class, MongoDBQueryObject.class);
                    break;
                default:
                    log.error("DB not found please use sqlite");
            }

        }
        else {
            log.warn("Trying to initialise the DB twice, this will be ignored!");
        }
    }

    private static boolean assertInitialisedDB() {
        return dbConnection == null;
    }

    @SneakyThrows
    public static QueryBuilder table(String tblName) {
        if (assertInitialisedDB()) {
            log.error(ERROR_MESSAGE);
            throw new DBError(ERROR_MESSAGE);
        }

        return new QueryBuilder(tblName);
    }

    public static void shutdown() {
        dbConnection.closeDB();
    }

    public static <T extends IQueryObject> T buildObject(Class<T> clazz, Object... args) {
        if (!objectMap.containsKey(clazz)) {
            log.error("Cannot find map for class : {}", clazz.getSimpleName());
            return null;
        }
        List<Class<?>> argClasses = Arrays.stream(args).map(Object::getClass).collect(Collectors.toList());
        argClasses.add(IConnection.class);
        List<Object> argOverride = new LinkedList<>(Arrays.asList(args));
        argOverride.add(dbConnection);
        try {
            //noinspection unchecked
            return (T) objectMap.get(clazz).getConstructor(argClasses.toArray(Class[]::new))
              .newInstance(argOverride.toArray());
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> T create(Class<T> clazz, Map<String, Object> values) {
        try {
            Method method = clazz.getSuperclass().getDeclaredMethod("create", IApplication.class, Class.class,
              Map.class, boolean.class);
            method.setAccessible(true);
            Map<String, String> newVal = new LinkedHashMap<>();
            values.forEach((k, v) -> newVal.put(k, String.valueOf(v)));
            T v = (T) method.invoke(null, dbConnection.getApplication(), clazz, newVal, false);
            if(v == null) {
                throw new UnsupportedOperationException("Model was not instrumented please, install agent!");
            }
            return v;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException e1) {
            e1.printStackTrace();
            throw new UnsupportedOperationException("An error occurred when creating model");
        }
    }

    public static <T extends Model> T createAndPersist(Class<T> clazz, Map<String, Object> values) {
        T model = create(clazz, values);
        model.save();
        return model;
    }

    @SneakyThrows
    public static void rawSQL(String tblName) {
        if (assertInitialisedDB()) {
            log.error(ERROR_MESSAGE);
            throw new DBError(ERROR_MESSAGE);
        }
        log.warn("Running raw sql, this can be a security risk please considering using the query builder.");

    }
}
