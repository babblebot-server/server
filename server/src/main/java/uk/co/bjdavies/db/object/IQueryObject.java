package uk.co.bjdavies.db.object;

import java.util.Map;
import java.util.Set;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface IQueryObject {
    IQueryObject columns(String... cols);

    <T> Set<T> get(Class<T> clazz);

    Set<Map<String, String>> get();
}
