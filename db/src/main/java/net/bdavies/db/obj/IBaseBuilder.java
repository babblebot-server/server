package net.bdavies.db.obj;

import java.util.Map;
import java.util.Set;

/**
 * This is includes standard Query functions
 *
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
public interface IBaseBuilder {
    IBaseBuilder where(String col, String val);

    IBaseBuilder and(String col, String val);

    IBaseBuilder or(String col, String val);

    IBaseBuilder where(String col, String operator, String val);

    IBaseBuilder and(String col, String operator, String val);

    IBaseBuilder or(String col, String operator, String val);

    <T> Set<T> get(Class<T> clazz);

    Set<Map<String, String>> get();

    Map<String, String> first();
    <T> T first(Class<T> clazz);
    Map<String, String> last();
    <T> T last(Class<T> clazz);

}
