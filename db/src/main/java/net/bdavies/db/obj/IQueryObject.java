package net.bdavies.db.obj;

import java.util.Map;
import java.util.Set;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface IQueryObject extends IBaseBuilder {
    IQueryObject columns(String... cols);

    IQueryObject where(String col, String val);

    IQueryObject and(String col, String val);

    IQueryObject or(String col, String val);

    IQueryObject where(String col, String operator, String val);

    IQueryObject and(String col, String operator, String val);

    IQueryObject or(String col, String operator, String val);

    <T> Set<T> get(Class<T> clazz);

    Set<Map<String, String>> get();

    boolean delete();
    boolean insert(Map<String, String> insertValues);
    int insertGetId(String idColName, Map<String, String> insertValues);
    int insertGetId(Map<String, String> insertValues);
    Map<String, String> insertGet(Map<String, String> insertValues);
    <T> T insertGet(Map<String, String> insertValues, Class<T> clazz);
    boolean update(Map<String, String> updateValues);
    Set<Map<String, String>> updateGet(Map<String, String> updateValues);
    <T> Set<T> updateGet(Map<String, String> updateValues, Class<T> clazz);
}
