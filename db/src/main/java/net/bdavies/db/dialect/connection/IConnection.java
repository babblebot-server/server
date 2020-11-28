package net.bdavies.db.dialect.connection;

import uk.co.bjdavies.api.IApplication;
import net.bdavies.db.query.PreparedQuery;

import java.util.Map;
import java.util.Set;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface IConnection {

    <T> Set<T> executeQuery(Class<T> clazz, String rawSql, PreparedQuery preparedQuery);

    Set<Map<String, String>> executeQueryRaw(String rawSql, PreparedQuery preparedQuery);

    IApplication getApplication();

    boolean executeCommand(String rawSql, PreparedQuery preparedQuery);
}
