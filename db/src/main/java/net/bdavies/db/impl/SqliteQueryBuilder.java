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

package net.bdavies.db.impl;

import net.bdavies.db.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class SqliteQueryBuilder<T extends IDBRecord> extends QueryBuilder<T> {

    public SqliteQueryBuilder(String primaryKey, String tableName, IConnection connection, Class<T> tClass) {
        super(primaryKey, tableName, connection, tClass);
    }

    @Override
    public String buildQuery() {
        String tableId = RandomStringUtils.randomAlphabetic(5);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT DISTINCT ")
                .append(columnsToString(tableId))
                .append(" FROM ")
                .append(this.tableName)
                .append(" AS ")
                .append("`").append(tableId).append("`");
        stringBuilder.append(buildWhereQueries(tableId));

        if (!this.orderColumn.equals("")) {
            stringBuilder.append(" ORDER BY ").append(tableId).append(".").append(this.orderColumn);
            if (!this.reverseOrder) {
                stringBuilder.append(" ASC");
            } else {
                stringBuilder.append(" DESC");
            }
        }
        //this has to be last
        if (this.limit != -1) {
            stringBuilder.append(" LIMIT ").append(this.limit);
        }

        return stringBuilder.toString();
    }

    private String buildWhereQueries(String tableId) {
        if (this.whereStatement == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(" WHERE ");
        sb.append(whereStatementToString(whereStatement, tableId));
        whereStatement.getGroup().forEach(ws -> {
            if (ws.getOperator() != null) {
                switch (ws.getOperator()) {
                    case AND:
                    case OR:
                        sb.append(appendWhereStatementsForOperator(ws, tableId, false));
                        break;
                }

            }
        });
        return sb.toString();
    }

    private String appendWhereStatementsForOperator(WhereStatement statement, String tableId, boolean grouped) {
        AtomicBoolean firstFound = new AtomicBoolean(true);
        StringBuilder sb = new StringBuilder();
        statement.getGroup().forEach(ws -> {
            if (ws.getOperator() != null) {
                if (grouped) {
                    sb.append(" ");
                    if (!firstFound.get()) {
                        sb.append(operatorToString(statement.getOperator()));
                    }
                    firstFound.set(false);
                    sb.append(" ( ");
                    sb.append(appendWhereStatementsForOperator(ws, tableId, true));
                    sb.append(" ) ");
                } else {
                    sb.append(" ");
                    sb.append(operatorToString(statement.getOperator()));
                    sb.append(" ( ");
                    sb.append(appendWhereStatementsForOperator(ws, tableId, true));
                    sb.append(" ) ");
                }
            } else {
                if (grouped) {
                    if (firstFound.get()) {
                        sb.append(whereStatementToString(ws, tableId));
                        firstFound.set(false);
                    } else {
                        sb.append(" ");
                        sb.append(operatorToString(statement.getOperator())).append(" ");
                        sb.append(whereStatementToString(ws, tableId));
                    }
                } else {
                    sb.append(" ");
                    sb.append(operatorToString(statement.getOperator())).append(" ");
                    sb.append(whereStatementToString(ws, tableId));
                }
            }
        });
        return sb.toString();
    }

    private String whereStatementToString(WhereStatement statement, String tableId) {
        StringBuilder sb = new StringBuilder();
        sb
                .append(tableId)
                .append(".")
                .append(statement.getKey())
                .append(comparatorToString(statement.getComparator()))
                .append("\"")
                .append(statement.getValue())
                .append("\"");

        return sb.toString();
    }

    private String operatorToString(Operator operator) {
        switch (operator) {
            case AND:
                return "AND";
            case OR:
                return "OR";
            default:
                return "";
        }
    }

    private String comparatorToString(Comparator comparator) {
        switch (comparator) {
            case EQUALS:
                return "=";
            case NOT_EQUALS:
                return "<>";
            case GREATER_THAN:
                return ">";
            case LESS_THAN:
                return "<";
            case GREATER_THAN_OR_EQUAL_TO:
                return ">=";
            case LESS_THAN_OR_EQUAL_TO:
                return "<=";
            case LIKE:
                return "LIKE";
            case NOT_LIKE:
                return "NOT LIKE";
            default:
                return "";
        }
    }

    private String columnsToString(String tableId) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.selectColumns.length; i++) {
            String column = this.selectColumns[i];
            if (column.contains("*")) {
                return "*";
            } else {
                stringBuilder.append(tableId).append(".").append(column)
                        .append(i < this.selectColumns.length - 1 ? "," : "");
            }
        }

        return stringBuilder.toString();
    }
}
