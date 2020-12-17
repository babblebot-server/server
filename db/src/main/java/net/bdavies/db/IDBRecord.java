package net.bdavies.db;

import java.util.Map;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IDBRecord {

    Object get(String key);

    Map<String, Object> getData();

    void setData(Map<String, Object> data);

    String getString(String key);

    int getInt(String key);

    float getFloat(String key);

    String toJsonString(String... keysToHide);

    boolean equals(String key, String value);
}
