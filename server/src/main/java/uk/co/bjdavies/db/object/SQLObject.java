package uk.co.bjdavies.db.object;

import lombok.extern.slf4j.Slf4j;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public abstract class SQLObject {

    public abstract String toSQLString();

}
