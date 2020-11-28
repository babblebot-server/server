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
        return "SELECT " + getColumnsString() + " FROM " + table + getWhereString();
    }

    protected String getWhereString() {
        if (!wheres.isEmpty()) {
            return wheres.stream().map(w -> w.toSQLString(null, preparedQuery))
              .collect(Collectors.joining());
        }
        return "";
    }
}
