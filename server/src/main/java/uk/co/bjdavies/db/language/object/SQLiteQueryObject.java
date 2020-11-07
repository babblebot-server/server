package uk.co.bjdavies.db.language.object;

import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.db.language.connection.IConnection;
import uk.co.bjdavies.db.object.QueryObject;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@ToString
public class SQLiteQueryObject extends QueryObject {

    public SQLiteQueryObject(@NonNull String table, @NonNull IConnection connection) {
        super(table, connection);
    }

    @Override
    public String toSQLString() {
        if (columns.size() == 0) { columns.add("*"); }
        return "SELECT " + (String.join(", ", columns)) + " FROM " + table;
    }
}
