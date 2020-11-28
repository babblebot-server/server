package net.bdavies.db.obj;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.query.PreparedQuery;
import net.bdavies.db.query.QueryType;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public abstract class SQLObject {

    public abstract String toSQLString(QueryType type, PreparedQuery query);

}
