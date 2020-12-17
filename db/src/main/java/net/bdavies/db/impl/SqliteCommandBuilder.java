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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class SqliteCommandBuilder extends CommandBuilder {

    public SqliteCommandBuilder(String tableName, IConnection connection) {
        super(tableName, connection);
    }

    static String getComparatorToString(Comparator comparator) {
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

    @Override
    public ISQLCommand buildCommand() {
        switch (type) {
            case INSERT:
                return buildInsertCommand();
            case UPDATE:
                return buildUpdateCommand();
            case DELETE:
                return buildDeleteCommand();
        }
        return new SQLCommand("", new ArrayList<>());
    }

    private ISQLCommand buildInsertCommand() {
        List<String> values = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO ")
                .append(this.tableName);

        stringBuilder.append("(");
        AtomicInteger i = new AtomicInteger();
        this.commandValues.forEach((key, value) -> {
            stringBuilder.append(key).append(i.get() < this.commandValues.keySet().size() - 1 ? ", " : "");
            values.add(String.valueOf(value));
            i.getAndIncrement();
        });
        stringBuilder.append(") VALUES (");

        for (int j = 0; j < this.commandValues.keySet().size(); j++) {
            stringBuilder.append("?").append(j < this.commandValues.keySet().size() - 1 ? "," : "");
        }
        stringBuilder.append(")");


        return new SQLCommand(stringBuilder.toString(), values);
    }

    private ISQLCommand buildUpdateCommand() {

        if (this.whereStatement == null) {
            throw new RuntimeException("You must supply a where statement for updating a row in the database");
        }

        List<String> values = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE ")
                .append(this.tableName);

        stringBuilder.append(" SET ");
        AtomicInteger i = new AtomicInteger();
        this.commandValues.forEach((key, value) -> {
            stringBuilder.append(key).append("=?").append(i.get() < this.commandValues.keySet().size() - 1 ? ", " : "");
            values.add(String.valueOf(value));
            i.getAndIncrement();
        });

        stringBuilder.append(buildWhereQueries(values));


        return new SQLCommand(stringBuilder.toString(), values);
    }

    private ISQLCommand buildDeleteCommand() {

        List<String> values = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DELETE FROM ")
                .append(this.tableName);

        stringBuilder.append(buildWhereQueries(values));


        return new SQLCommand(stringBuilder.toString(), values);
    }

    private String buildWhereQueries(List<String> values) {
        if (this.whereStatement == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(" WHERE ");
        sb.append(whereStatementToString(whereStatement, values));
        whereStatement.getGroup().forEach(ws -> {
            if (ws.getOperator() != null) {
                switch (ws.getOperator()) {
                    case AND:
                    case OR:
                        sb.append(appendWhereStatementsForOperator(ws, values, false));
                        break;
                }

            }
        });
        return sb.toString();
    }

    @SuppressWarnings("DuplicatedCode")
    private String appendWhereStatementsForOperator(WhereStatement statement, List<String> values, boolean grouped) {
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
                    sb.append(appendWhereStatementsForOperator(ws, values, true));
                    sb.append(" ) ");
                } else {
                    sb.append(" ");
                    sb.append(operatorToString(statement.getOperator()));
                    sb.append(" ( ");
                    sb.append(appendWhereStatementsForOperator(ws, values, true));
                    sb.append(" ) ");
                }
            } else {
                if (grouped) {
                    if (firstFound.get()) {
                        sb.append(whereStatementToString(ws, values));
                        firstFound.set(false);
                    } else {
                        sb.append(" ");
                        sb.append(operatorToString(statement.getOperator())).append(" ");
                        sb.append(whereStatementToString(ws, values));
                    }
                } else {
                    sb.append(" ");
                    sb.append(operatorToString(statement.getOperator())).append(" ");
                    sb.append(whereStatementToString(ws, values));
                }
            }
        });
        return sb.toString();
    }

    private String whereStatementToString(WhereStatement statement, List<String> values) {
        StringBuilder sb = new StringBuilder();
        sb
                .append(statement.getKey())
                .append(comparatorToString(statement.getComparator()))
                .append("?");
        values.add(statement.getValue());

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
        return getComparatorToString(comparator);
    }
}
