package uk.co.bjdavies.db;

import uk.co.bjdavies.api.db.*;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public abstract class BaseBuilder implements IBaseBuilder {

    protected final String primaryKey;
    protected final String tableName;
    protected final IConnection connection;
    protected String[] selectColumns;
    protected WhereStatement whereStatement = null;

    public BaseBuilder(String primaryKey, String tableName, IConnection connection) {
        this.primaryKey = primaryKey;
        this.tableName = tableName;
        this.connection = connection;
    }

    @Override
    public IBaseBuilder where(String key, Object value) {
        return this.where(key, Comparator.EQUALS, value);
    }

    @Override
    public IBaseBuilder where(String key, Comparator comparator, Object value) {
        return this.where(new WhereStatement(key, String.valueOf(value), comparator));
    }

    @Override
    public IBaseBuilder where(WhereStatement statement) {
        if (this.whereStatement != null) {
            this.and(statement);
        } else {
            this.whereStatement = statement;
        }
        return this;
    }

    @Override
    public IBaseBuilder and(WhereStatement... statement) {
        if (this.whereStatement == null) {
            throw new IllegalArgumentException("You need to make sure you call where first before you can call and.");
        }
        WhereStatement statement1 = new WhereStatement(Operator.AND, statement);
        this.whereStatement.add(statement1);
        return this;
    }

    @Override
    public IBaseBuilder or(WhereStatement... statement) {
        if (this.whereStatement == null) {
            throw new IllegalArgumentException("You need to make sure you call where first before you can call or.");
        }
        WhereStatement statement1 = new WhereStatement(Operator.OR, statement);
        this.whereStatement.add(statement1);
        return this;
    }
}
