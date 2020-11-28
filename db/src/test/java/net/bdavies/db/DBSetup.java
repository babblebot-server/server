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

import net.bdavies.db.dialect.connection.IConnection;
import net.bdavies.db.dialect.connection.InMemoryConnection;
import net.bdavies.db.dialect.obj.SQLiteQueryObject;
import net.bdavies.db.mock.ApplicationMock;
import net.bdavies.db.model.TestModel;
import net.bdavies.db.obj.QueryObject;
import net.bdavies.db.query.PreparedQuery;
import net.bdavies.db.query.QueryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.bjdavies.api.IApplication;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * This is a abstract class to setup a in-memory db
 *
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
@ExtendWith(MockitoExtension.class)
public abstract class DBSetup {

    protected IConnection connection;
    protected IApplication application;

    @BeforeEach
    void setUp() {

        application = new ApplicationMock();
        connection = new InMemoryConnection(new InMemoryDatabaseConfig(), application);
        try {
            Field dbConnection = DB.class.getDeclaredField("dbConnection");
            dbConnection.setAccessible(true);
            dbConnection.set(null, connection);
            Field objectMap = DB.class.getDeclaredField("objectMap");
            objectMap.setAccessible(true);
            //noinspection unchecked
            ((Map<Object, Object>)objectMap.get(null)).put(QueryObject.class, SQLiteQueryObject.class);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        createTestTable();
        populateTestTable(9);
    }

    protected void populateTestTable(int number) {
        if (number > 9) number = 9;
        List<String> names = new LinkedList<>();
        names.add("John");
        names.add("Pete");
        names.add("Harry");
        names.add("Jo");
        names.add("Joey");
        names.add("Sam");
        names.add("Rob");
        names.add("Robbie");
        names.add("Lex");
        for (int i = 0; i < number; i++ ) {
            QueryBuilder queryBuilder = new QueryBuilder("test");
            queryBuilder.columns("text").insert(Map.of("text", names.get(i)));
        }
    }

    private void createTestTable() {
        String sql = "CREATE TABLE IF NOT EXISTS test (id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT NOT NULL);";
        connection.executeCommand(sql, new PreparedQuery());
    }


    protected TestModel createTestModel(String text) {
        try {
            Method method = TestModel.class.getSuperclass().getDeclaredMethod("create", IApplication.class, Class.class,
                    Map.class, boolean.class);
            method.setAccessible(true);
            return (TestModel) method.invoke(null, application, TestModel.class, Map.of("text", text), false);
        } catch (Exception e) {
            return null;
        }
    }

    @AfterEach
    void tearDown() {
        ((InMemoryConnection)connection).closeConnection();
    }


}
