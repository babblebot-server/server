package uk.co.bjdavies.db.object;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.db.language.connection.IConnection;

import java.util.*;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public abstract class QueryObject extends SQLObject implements IQueryObject {
    @NonNull
    protected String table;
    protected List<String> columns = new LinkedList<>();

    @NonNull
    private final IConnection connection;

    @Override
    public IQueryObject columns(String... cols) {
        columns.addAll(Arrays.asList(cols));
        return this;
    }

    @Override
    public <T> Set<T> get(Class<T> clazz) {
        return connection.executeQuery(clazz, this.toSQLString());
    }

    @Override
    public Set<Map<String, String>> get() {
        return connection.executeQueryRaw(this.toSQLString());
    }
}
