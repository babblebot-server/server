package uk.co.bjdavies.db.language.connection;

import java.util.Map;
import java.util.Set;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface IConnection {

    <T> Set<T> executeQuery(Class<T> clazz, String rawSql); //TODO: Support prepare

    Set<Map<String, String>> executeQueryRaw(String rawSql); //TODO: Support prepare
}
