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

package net.bdavies.db.dialect.connection;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.config.IDatabaseConfig;
import net.bdavies.db.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Connection class for the MySQL Driver
 * <p>
 * Used for MySQL configurations setup by the user
 *
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
public class MySQLConnection extends RDMSConnection {

    /**
     * Construct a MySQLConnection
     *
     * @param manager The database manager that controls this connection
     */
    public MySQLConnection(DatabaseManager manager) {
        super(manager);
    }

    /**
     * Set up the underlying MySQL Client
     *
     * @param config The database config setup by the user
     * @param application The application interface
     * @return {@link Connection} sql connection
     */
    @Override
    @SneakyThrows
    protected Connection getConnectionForDB(IDatabaseConfig config, IApplication application) {
        return DriverManager.getConnection("jdbc:log4jdbc:mysql://" + config.getHostname() + ":" +
                (config.getPort().isEmpty() ? "3306" : config.getPort()) +  "/" +
                config.getDatabase() + "?user=" + config.getUsername() +
                "&password=" + config.getPassword());
    }
}
