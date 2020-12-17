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

package net.bdavies.db;

import lombok.SneakyThrows;

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
