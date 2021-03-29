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

package net.bdavies.db.dialect.obj;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.DatabaseManager;
import net.bdavies.db.Operator;
import net.bdavies.db.dialect.connection.IConnection;
import net.bdavies.db.model.Model;
import net.bdavies.db.obj.IQueryObject;
import net.bdavies.db.obj.ISQLObject;
import net.bdavies.db.obj.QueryObject;
import net.bdavies.db.obj.WhereStatementObject;
import net.bdavies.db.query.PreparedQuery;
import net.bdavies.db.query.QueryLink;
import net.bdavies.db.query.QueryType;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Base class for SQL type queries
 * <p>
 * Used as a default SQL language for SQL type queries all methods can be overriden
 *
 * @author me@bdavies (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
@AllArgsConstructor
@ToString(callSuper = true)
public class BaseQueryObject extends QueryObject implements ISQLObject
{
    @NonNull
    protected final String table;
    @NonNull
    protected final IConnection<String> connection;
    @NonNull
    protected final DatabaseManager manager;


    /**
     * Get
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T extends Model> Set<T> get(Class<T> clazz)
    {
        return connection
                .executeQuery(clazz, this.toSQLString(QueryType.SELECT, preparedQuery), preparedQuery);
    }

    @Override
    public Set<Map<String, String>> get()
    {
        return connection.executeQueryRaw(this.toSQLString(QueryType.SELECT, preparedQuery), preparedQuery);
    }

    @Override
    public boolean delete()
    {
        return connection.executeCommand(this.toSQLString(QueryType.DELETE, preparedQuery), preparedQuery);
    }

    @Override
    public boolean insert(Map<String, String> insertValues)
    {
        this.values = insertValues;
        return connection.executeCommand(this.toSQLString(QueryType.INSERT, preparedQuery), preparedQuery);
    }


    @Override
    public boolean update(Map<String, String> updateValues)
    {
        this.values = updateValues;
        return connection.executeCommand(this.toSQLString(QueryType.UPDATE, preparedQuery), preparedQuery);
    }

    @Override
    public String toSQLString(QueryType type, PreparedQuery query)
    {
        PreparedQuery queryToUse = query == null ? preparedQuery : query;
        switch (type)
        {
            case SELECT:
                return toSelectQuery(queryToUse);
            case INSERT:
                return toInsertQuery(queryToUse);
            case UPDATE:
                return toUpdateQuery(queryToUse);
            case DELETE:
                return toDeleteString(queryToUse);
            case NONE:
                return getWhereString(queryToUse);
            default:
                return "";
        }
    }

    private String toUpdateQuery(PreparedQuery queryToUse)
    {
        return "UPDATE " + table + " SET " + values.entrySet().stream().map(e -> "`" + e.getKey() + "`" +
                " = " + queryToUse.getValueFromString(e.getValue()))
                .collect(Collectors.joining(", ")) + " " + getWhereString(queryToUse);
    }

    protected String toDeleteString(PreparedQuery queryToUse)
    {
        return "DELETE FROM " + table + getWhereString(queryToUse);
    }

    protected String toInsertQuery(PreparedQuery queryToUse)
    {
        return "INSERT INTO " + table + " (" + getColumnsString() + ") VALUES (" +
                getValuesString(queryToUse) + ")";
    }

    private String getColumnsString()
    {
        if (columns.isEmpty())
        {
            columns.add("*");
        }
        return columns.stream().map(c -> c.equals("*") ? c : ("`" + c + "`"))
                .collect(Collectors.joining(", "));
    }

    private String getValuesString(PreparedQuery query)
    {
        return columns.stream().map(c -> query.getValueFromString(values.get(c)))
                .collect(Collectors.joining(", "));
    }


    protected String toSelectQuery(PreparedQuery queryToUse)
    {
        return "SELECT " + getColumnsString() + " FROM " + table + getWhereString(queryToUse) +
                getOrderByString();
    }

    private String getOrderByString()
    {
        if (orderMap.isEmpty())
        {
            return "";
        }

        return " ORDER BY " + orderMap.entrySet().stream().map(e -> e.getKey() + " " + e.getValue())
                .collect(Collectors.joining(", "));
    }

    protected String getWhereString(PreparedQuery queryToUse)
    {
        if (!wheres.isEmpty())
        {
            if (wheres.stream().anyMatch(w -> w.getValue() instanceof GroupValue))
            {
                log.error("Wheres: {}", wheres);
            }
            return wheres.stream().map(w -> w.toSQLString(null, queryToUse))
                    .collect(Collectors.joining());
        }
        return "";
    }

    @Override
    public BaseQueryObject group(IQueryObject object, QueryLink link)
    {
        preparedQuery.getPreparedValues()
                .addAll(((BaseQueryObject) object).preparedQuery.getPreparedValues());
        wheres.add(new WhereStatementObject("", Operator.NONE, new GroupValue((ISQLObject) object), link));
        return this;
    }

    @Override
    public BaseQueryObject group(Consumer<IQueryObject> builder,
                                 QueryLink link)
    {
        IQueryObject object = manager.createQueryBuilder(table);
        builder.accept(object);
        return group(object, link);
    }
}
