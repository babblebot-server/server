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

import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.dialect.connection.IConnection;
import net.bdavies.db.dialect.connection.InMemoryConnection;
import net.bdavies.db.obj.IQueryObject;
import net.bdavies.db.query.PreparedQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
public abstract class DBSetup
{
    protected DatabaseManager manager;

    @BeforeEach
    protected void setUp()
    {
        DatabaseConfig config = DatabaseConfig.builder().type("sqlite").database("in-memory").build();
        manager = new DatabaseManager(config, new ApplicationMock(config), InMemoryConnection.class);
        createTestTable();
        populateTestTable();
    }

    protected void populateTestTable()
    {
        int number = 9;
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
        for (int i = 0; i < number; i++)
        {
            IQueryObject object = manager.createQueryBuilder("test");
            object.columns("text").insert(Map.of("text", names.get(i)));
        }
    }

    private void createTestTable()
    {
        String sql = "CREATE TABLE IF NOT EXISTS test (id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT NOT " +
                "NULL);";
        //noinspection unchecked
        ((IConnection<String>)manager.getConnection()).executeCommand(sql, new PreparedQuery());
    }


    @AfterEach
    void tearDown()
    {
        try
        {
            manager.shutdown();
        } catch (Exception e) {
            log.error("Already closed!!", e);
        }
    }
}
