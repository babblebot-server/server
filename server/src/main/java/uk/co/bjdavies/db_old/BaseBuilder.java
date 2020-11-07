
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

package uk.co.bjdavies.db_old;

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
