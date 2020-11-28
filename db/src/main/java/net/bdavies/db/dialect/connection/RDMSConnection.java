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

package net.bdavies.db.dialect.connection;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.config.IDatabaseConfig;
import net.bdavies.db.query.PreparedQuery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Edit me
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public abstract class RDMSConnection implements IConnection {

    protected final Connection connection;
    protected final IApplication application;

    @SneakyThrows
    public RDMSConnection(IDatabaseConfig config, IApplication application) {
        this.connection = getConnectionForDB(config, application);
        this.application = application;
    }

    protected abstract Connection getConnectionForDB(IDatabaseConfig config, IApplication application);

    @Override
    public <T> Set<T> executeQuery(Class<T> clazz, String rawSql, PreparedQuery query) {
        try(PreparedStatement statement = connection.prepareStatement(rawSql)) {
            setValues(query, statement);
            return processResultSet(statement.executeQuery(), clazz);
        } catch (SQLException e) {
            log.error("An Error occurred when running command: {}", rawSql, e);
        }
        return new LinkedHashSet<>();
    }

    protected void setValues(PreparedQuery query, PreparedStatement statement) {
        AtomicInteger integer = new AtomicInteger(1);
        query.getPreparedValues().forEach(v -> {
            try {
                statement.setString(integer.getAndIncrement(), v);
            } catch (SQLException e) {
               log.error("Couldn't set value: {}, because of an error", v, e);
            }
        });
    }

    @Override
    public Set<Map<String, String>> executeQueryRaw(String rawSql, PreparedQuery query) {
        try(PreparedStatement statement = connection.prepareStatement(rawSql)) {
            setValues(query, statement);
            return processResultSetRaw(statement.executeQuery());
        } catch (SQLException e) {
            log.error("An Error occurred when running command: {}", rawSql, e);
        }
        return new LinkedHashSet<>();
    }

    @Override
    public boolean executeCommand(String rawSql, PreparedQuery query) {
        try(PreparedStatement statement = connection.prepareStatement(rawSql)) {
            setValues(query, statement);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("An Error occurred when running command", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Set<T> processResultSet(ResultSet resultSet, Class<T> clazz) {
        Set<Map<String, String>> values = processResultSetRaw(resultSet);
        //noinspection OverlyLongLambda
        return values.stream().map(v -> {
            try {
                Method method = clazz.getSuperclass().getDeclaredMethod("create", IApplication.class, Class.class,
                  Map.class, boolean.class);
                method.setAccessible(true);
                return (T) method.invoke(null, application, clazz, v, true);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
              | InvocationTargetException e1) {
                e1.printStackTrace();
                return null;
            }
        }).collect(Collectors.toSet());
    }

    @SuppressWarnings("MethodWithMultipleLoops")
    private Set<Map<String, String>> processResultSetRaw(ResultSet resultSet) {
        Set<Map<String, String>> set = new LinkedHashSet<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, String> objectMap = new HashMap<>();
                for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(columnName);
                    objectMap.put(columnName, columnValue);
                }

                set.add(objectMap);
            }
            resultSet.close();
        } catch (Exception e) {
            log.error("Error selecting all from the table, most likely the table has not been created.", e);
            return set;
        }
        return set;
    }

    @Override
    public IApplication getApplication() {
        return application;
    }
}
