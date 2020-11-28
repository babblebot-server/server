package net.bdavies.db.obj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.query.PreparedQuery;
import net.bdavies.db.query.QueryLink;
import net.bdavies.db.query.QueryType;

/**
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
@Slf4j
@Getter
@AllArgsConstructor
public class WhereStatementObject extends SQLObject {
    private final String column;
    private final String operator;
    private final String value;
    private final QueryLink link;

    @Override
    public String toSQLString(QueryType type, PreparedQuery query) {
        return " " + (link == null ? "WHERE " : link.name() + " ") + "`" + column + "`" + " " + operator + " " +
                query.getValueFromString(value);
    }
}
