package net.bdavies.db.query;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * This will hold utility classes for the current query planning on getting executed
 *
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
@Slf4j
@Getter
public class PreparedQuery {
    private final List<String> preparedValues = new LinkedList<>();

    public void addPreparedValue(String v) {
        preparedValues.add(v);
    }

    private boolean shouldPrepare(String val) {
        return val != null && !NumberUtils.isCreatable(val);
    }
    public String getValueFromString(String val) {
        if(shouldPrepare(val)) {
            addPreparedValue(val);
            return "?";
        }
        return val;
    }
}
