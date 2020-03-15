package uk.co.bjdavies.db;

import lombok.extern.log4j.Log4j2;
import uk.co.bjdavies.api.db.IDBRecord;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public class DBRecord implements IDBRecord {

    private Map<String, Object> data;

    public DBRecord(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public Object get(String key) {
        if (!data.containsKey(key)) {
            log.error("Column: " + key + " does not exist in this table.");
        } else {
            return data.get(key);
        }
        return null;
    }

    @Override
    public Map<String, Object> getData() {
        return this.data;
    }

    @Override
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String getString(String key) {
        return String.valueOf(get(key));
    }

    @Override
    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    @Override
    public float getFloat(String key) {
        return Float.parseFloat(getString(key));
    }

    @Override
    public String toJsonString(String... keysToHide) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        data.forEach((key, value) -> {
            if (Arrays.stream(keysToHide).noneMatch(e1 -> e1.toLowerCase().equals(key.toLowerCase()))) {
                stringBuilder.append("\"").append(key).append("\":").append((value instanceof String) ? "\"" + value + "\"" : value);

                int index = getIndex(data.keySet(), key);
                if (!(index == data.size() - 1)) {
                    stringBuilder.append(",");
                }
            }
        });
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    /**
     * This will return the current index of a set.
     *
     * @param set   - The set to check.
     * @param value - The current set's indexes value.
     * @return int
     */
    private int getIndex(Set<?> set, Object value) {
        int result = 0;
        for (Object entry : set) {
            if (entry.equals(value)) return result;
            result++;
        }
        return -1;
    }


    @Override
    public boolean equals(String key, String value) {
        if (data.containsKey(key)) {
            return data.entrySet().stream().anyMatch(s -> s.getKey().equals(key) && s.getValue().equals(value));
        }
        return false;
    }
}
