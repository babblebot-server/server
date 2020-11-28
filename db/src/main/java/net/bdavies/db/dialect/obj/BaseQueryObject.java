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

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.query.QueryType;
import net.bdavies.db.dialect.connection.IConnection;
import net.bdavies.db.query.PreparedQuery;
import net.bdavies.db.obj.QueryObject;

import java.util.stream.Collectors;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class BaseQueryObject extends QueryObject {

    public BaseQueryObject(@NonNull String table,
      @NonNull IConnection connection) {
        super(table, connection);
    }

    @Override
    public String toSQLString(QueryType type, PreparedQuery query) {
        switch (type) {
            case SELECT:
                return toSelectQuery();
            case INSERT:
                return toInsertQuery();
            case UPDATE:
                return toUpdateQuery();
            case DELETE:
                return toDeleteString();
            default:
                return "";
        }
    }

    private String toUpdateQuery() {
        return "UPDATE " + table + " SET " + values.entrySet().stream().map(e -> "`" + e.getKey() + "`" +
                " = " + preparedQuery.getValueFromString(e.getValue()))
                .collect(Collectors.joining(", ")) + " " + getWhereString();
    }

    protected String toDeleteString() {
        return "DELETE FROM " + table + getWhereString();
    }

    protected String toInsertQuery() {
        return "INSERT INTO "+table+" (" + getColumnsString() + ") VALUES (" + getValuesString() + ")";
    }

    private String getColumnsString() {
        if (columns.isEmpty()) columns.add("*");
        return columns.stream().map(c -> c.equals("*") ? c : "`" + c + "`").collect(Collectors.joining(", "));
    }

    private String getValuesString() {
        return columns.stream().map(c -> preparedQuery.getValueFromString(values.get(c)))
                .collect(Collectors.joining(", "));
    }


    protected String toSelectQuery() {
        return "SELECT " + getColumnsString() + " FROM " + table + getWhereString() + getOrderByString();
    }

    private String getOrderByString() {
        if (orderMap.isEmpty()) return "";

        return " ORDER BY " + orderMap.entrySet().stream().map(e -> e.getKey() + " " + e.getValue())
                .collect(Collectors.joining(", "));
    }

    protected String getWhereString() {
        if (!wheres.isEmpty()) {
            return wheres.stream().map(w -> w.toSQLString(null, preparedQuery))
              .collect(Collectors.joining());
        }
        return "";
    }
}
