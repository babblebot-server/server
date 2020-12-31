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
import net.bdavies.api.IApplication;
import net.bdavies.api.config.IDatabaseConfig;
import net.bdavies.db.DatabaseManager;
import net.bdavies.db.model.Model;
import net.bdavies.db.query.PreparedQuery;

import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Edit me
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public abstract class RDMSConnection implements IConnection<String>
{

    protected final Connection connection;
    protected final DatabaseManager manager;

    @SneakyThrows
    protected RDMSConnection(DatabaseManager manager)
    {
        this.manager = manager;
        this.connection = getConnectionForDB(manager.getApplication().getConfig().getDatabaseConfig(),
                manager.getApplication());
    }

    protected abstract Connection getConnectionForDB(IDatabaseConfig config, IApplication application);

    @Override
    public <T extends Model> Set<T> executeQuery(Class<T> clazz, String rawSql, PreparedQuery query)
    {
        if (rawSql.contains("("))
        {
            log.error("Running SQL: {}", rawSql);
        }
        try (PreparedStatement statement = connection.prepareStatement(rawSql))
        {
            setValues(query, statement);
            return processResultSet(statement.executeQuery(), clazz, query);
        }
        catch (SQLException e)
        {
            log.error("An Error occurred when running command: {}", rawSql, e);
        }
        return new LinkedHashSet<>();
    }

    protected void setValues(PreparedQuery query, PreparedStatement statement)
    {
        AtomicInteger integer = new AtomicInteger(1);
        query.getPreparedValues().forEach(v -> {
            try
            {
                statement.setString(integer.getAndIncrement(), v);
            }
            catch (SQLException e)
            {
                log.error("Couldn't set value: {}, because of an error", v, e);
            }
        });
        query.getPreparedValues().clear();
    }

    @Override
    public Set<Map<String, String>> executeQueryRaw(String rawSql, PreparedQuery query)
    {
        try (PreparedStatement statement = connection.prepareStatement(rawSql))
        {
            setValues(query, statement);
            return processResultSetRaw(statement.executeQuery());
        }
        catch (SQLException e)
        {
            log.error("An Error occurred when running command: {}", rawSql, e);
        }
        return new LinkedHashSet<>();
    }

    @Override
    public boolean executeCommand(String rawSql, PreparedQuery query)
    {
        try (PreparedStatement statement = connection.prepareStatement(rawSql))
        {
            setValues(query, statement);
            return statement.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            log.error("An Error occurred when running command", e);
            return false;
        }
    }

    private <T extends Model> Set<T> processResultSet(ResultSet resultSet, Class<T> clazz,
                                                      PreparedQuery query)
    {
        Set<Map<String, String>> values = processResultSetRaw(resultSet);
        //noinspection DuplicatedCode
        return values.stream().map(v -> {
            Map<String, Object> vTransform = v.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                    Map.Entry::getValue));
            T model = createModel(clazz);
            model.init(manager, vTransform, true);
            log.info("Model JSON: {}", model.toJson());

            return model;
        }).filter(m -> {
            //TODO: Awful method of doing this, create a dummy model and create sql based on that!
            //noinspection unchecked
            return query.getModelPredicates().stream().map(p -> (Predicate<T>) p)
                    .reduce(x -> true, Predicate::and).test(m);
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @SneakyThrows
    private static <T extends Model> T createModel(Class<T> clazz)
    {
        Constructor<? extends T> constructor = clazz.getDeclaredConstructor();
        return constructor.newInstance();
    }

    @SuppressWarnings("MethodWithMultipleLoops")
    private Set<Map<String, String>> processResultSetRaw(ResultSet resultSet)
    {
        Set<Map<String, String>> set = Collections.synchronizedSet(new LinkedHashSet<>());
        try
        {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next())
            {
                Map<String, String> objectMap = new HashMap<>();
                for (int i = 1; i < metaData.getColumnCount() + 1; i++)
                {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(columnName);
                    objectMap.put(columnName, columnValue);
                }

                set.add(objectMap);
            }
            resultSet.close();
        }
        catch (Exception e)
        {
            log.error("Error selecting all from the table, most likely the table has not been created.", e);
            return set;
        }
        return set;
    }

    @Override
    public IApplication getApplication()
    {
        return manager.getApplication();
    }

    @SneakyThrows
    @Override
    public void closeDB()
    {
        connection.close();
    }
}
