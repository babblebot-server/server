/*
 * MIT License
 *
 * Copyright (c) 2020 Ben Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

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
