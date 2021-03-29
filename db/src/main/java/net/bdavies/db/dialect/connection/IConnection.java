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

import net.bdavies.api.IApplication;
import net.bdavies.db.dialect.descriptor.MongoDocumentDescriptor;
import net.bdavies.db.model.Model;
import net.bdavies.db.query.PreparedQuery;

import java.util.Map;
import java.util.Set;

/**
 * Interface describing a connections public methods
 * <p>
 * For executing queries on a DB connection
 * <p>
 * {@code Set<Model> models = manager.getConnection().executeQuery(Model.class, "SELECT * FROM example", new
 * PreparedQuery());}
 *
 * @author me@bdavies (Ben Davies)
 * @since __RELEASE_VERSION__
 */
public interface IConnection<R>
{

    /**
     * Execute a query and get a Model set back
     *
     * @param clazz         The class to get back
     * @param obj           The query to run {@link String} for SQL, {@link MongoDocumentDescriptor} for
     *                      a Mongo Connection
     * @param preparedQuery The query data
     * @param <T>           Type of class to return
     * @return {@link Set}  collection of Models
     */
    <T extends Model> Set<T> executeQuery(Class<T> clazz, R obj, PreparedQuery preparedQuery);

    /**
     * Execute a query and get a Map set back
     *
     * @param obj           The query to run {@link String} for SQL, {@link MongoDocumentDescriptor} for
     *                      a Mongo Connection
     * @param preparedQuery The query data
     * @return {@link Set}  collection of Maps
     */
    Set<Map<String, String>> executeQueryRaw(R obj, PreparedQuery preparedQuery);

    /**
     * Get the application interface
     *
     * @return {@link IApplication} application interface
     */
    IApplication getApplication();

    /**
     * Execute a DB Command
     *
     * @param obj           The query to run {@link String} for SQL, {@link MongoDocumentDescriptor} for
     *                      a Mongo Connection
     * @param preparedQuery The query data
     * @return {@link Boolean} true if successful
     */
    boolean executeCommand(R obj, PreparedQuery preparedQuery);

    /**
     * Close the underlying connection to the database
     */
    void closeDB();
}
