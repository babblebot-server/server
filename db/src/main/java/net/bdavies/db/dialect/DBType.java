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

package net.bdavies.db.dialect;

import lombok.Getter;
import net.bdavies.api.config.IDatabaseConfig;
import net.bdavies.db.dialect.connection.IConnection;
import net.bdavies.db.dialect.connection.MongoDBConnection;
import net.bdavies.db.dialect.connection.MySQLConnection;
import net.bdavies.db.dialect.connection.SQLiteConnection;
import net.bdavies.db.dialect.obj.MongoDBQueryObject;
import net.bdavies.db.dialect.obj.MySQLQueryObject;
import net.bdavies.db.dialect.obj.SQLiteQueryObject;
import net.bdavies.db.obj.IQueryObject;

import java.util.Locale;

/**
 * Enum for all the supported Database types
 * <p>
 * Get a type based on a string using {@link #find(String)}
 * <p>
 * DBType type = DBType.find("mysql");
 * <p>
 * IConnection connection = application.get(type.getConnectionClass());
 *
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Getter
public enum DBType
{
    MONGODB(MongoDBConnection.class, MongoDBQueryObject.class),
    SQLITE(SQLiteConnection.class, SQLiteQueryObject.class),
    MYSQL(MySQLConnection.class, MySQLQueryObject.class);

    private final Class<? extends IConnection<?>> connectionClass;
    private final Class<? extends IQueryObject> queryObjectClass;

    /**
     * Constructor for DBType
     *
     * @param connectionClass  Connection class for this type
     * @param queryObjectClass Query object class fro this type
     */
    DBType(Class<? extends IConnection<?>> connectionClass, Class<? extends IQueryObject> queryObjectClass)
    {
        this.connectionClass = connectionClass;
        this.queryObjectClass = queryObjectClass;
    }

    /**
     * Convert type string to DBType
     *
     * @param type type string from a {@link IDatabaseConfig#getType()}
     * @return {@link DBType} a db type from a string
     */
    public static DBType find(String type)
    {
        return DBType.valueOf(type.toUpperCase(Locale.ROOT));
    }
}
