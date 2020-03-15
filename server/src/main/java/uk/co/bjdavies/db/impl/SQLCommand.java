package uk.co.bjdavies.db.impl;

import uk.co.bjdavies.api.db.ISQLCommand;

import java.util.List;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class SQLCommand implements ISQLCommand {

    private final String sql;
    private final List<String> values;

    public SQLCommand(String sql, List<String> values) {
        this.sql = sql;
        this.values = values;
    }

    @Override
    public String getSQL() {
        return sql;
    }

    @Override
    public List<String> getValues() {
        return values;
    }
}
