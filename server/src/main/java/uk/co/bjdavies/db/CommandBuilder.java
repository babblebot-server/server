package uk.co.bjdavies.db;

import lombok.SneakyThrows;
import uk.co.bjdavies.api.db.Comparator;
import uk.co.bjdavies.api.db.ICommandBuilder;
import uk.co.bjdavies.api.db.IConnection;
import uk.co.bjdavies.api.db.WhereStatement;

import java.util.Map;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public abstract class CommandBuilder extends BaseBuilder implements ICommandBuilder {

    protected CommandType type;
    protected Map<String, Object> commandValues;

    public CommandBuilder(String tableName, IConnection connection) {
        super("", tableName, connection);
    }

    @Override
    public ICommandBuilder where(String key, Object value) {
        return (ICommandBuilder) super.where(key, value);
    }

    @Override
    public ICommandBuilder where(String key, Comparator comparator, Object value) {
        return (ICommandBuilder) super.where(key, comparator, value);
    }

    @Override
    public ICommandBuilder where(WhereStatement statement) {
        return (ICommandBuilder) super.where(statement);
    }

    @Override
    public ICommandBuilder and(WhereStatement... statement) {
        return (ICommandBuilder) super.and(statement);
    }

    @Override
    public ICommandBuilder or(WhereStatement... statement) {
        return (ICommandBuilder) super.or(statement);
    }

    @SneakyThrows
    @Override
    public boolean insert(Map<String, Object> insertValues) {
        type = CommandType.INSERT;
        this.commandValues = insertValues;
        return (boolean) connection.executeCommand(this);
    }

    @SneakyThrows
    @Override
    public boolean update(Map<String, Object> updateValues) {
        this.commandValues = updateValues;
        type = CommandType.UPDATE;
        return (boolean) connection.executeCommand(this);
    }

    @SneakyThrows
    @Override
    public boolean delete() {
        type = CommandType.DELETE;
        return (boolean) connection.executeCommand(this);
    }

    protected enum CommandType {
        INSERT,
        UPDATE,
        DELETE
    }
}
