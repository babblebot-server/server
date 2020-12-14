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
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.config.IDatabaseConfig;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Connection class for a MySQL connection
 *
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
@Slf4j
public class MySQLConnection extends RDMSConnection {

    public MySQLConnection(IDatabaseConfig config, IApplication application) {
        super(config, application);
    }

    @Override
    @SneakyThrows
    protected Connection getConnectionForDB(IDatabaseConfig config, IApplication application) {
        try {
            Class.forName("com.mysql.jdbc.Driver"); //NOSONAR
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection("jdbc:log4jdbc:mysql://" + config.getHostname() + ":" +
                (config.getPort().isEmpty() ? "3306" : config.getPort()) +  "/" + config.getDatabase() + "?user=" + config.getUsername() +
                "&password=" + config.getPassword());
    }
}
