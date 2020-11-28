package net.bdavies.db.dialect.obj;

import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.dialect.connection.IConnection;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
@ToString
public class SQLiteQueryObject extends BaseQueryObject {

    public SQLiteQueryObject(@NonNull String table, @NonNull IConnection connection) {
        super(table, connection);
    }

}
