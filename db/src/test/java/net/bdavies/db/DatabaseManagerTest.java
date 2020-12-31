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

import net.bdavies.db.dialect.DBType;
import net.bdavies.db.dialect.connection.IConnection;
import net.bdavies.db.dialect.connection.SQLiteConnection;
import net.bdavies.db.model.TestModel;
import net.bdavies.db.obj.IQueryObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
class DatabaseManagerTest extends DBSetup
{

    @Test
    void construct() {
        assertThrows(NullPointerException.class,
                () -> new DatabaseManager(null, null), "It should throw null pointer exception!");


        //noinspection ConstantConditions
        assertThrows(NullPointerException.class,
                () -> new DatabaseManager(null, null, null),
                "It should throw null pointer exception!");

        DatabaseConfig config = DatabaseConfig.builder().type("sqlite").database("deleteme.db").build();
        DatabaseManager manager2 = new DatabaseManager(config, new ApplicationMock(config));
        assertTrue(manager2.getConnection() instanceof SQLiteConnection,
                "Config is of type sqlite so the connection should match that");
        File file = new File(config.getDatabase());
        assertTrue(file.exists(), "Delete me database file should exist!!");
        manager2.shutdown();
        file.delete();

        assertThrows(NullPointerException.class,
                () -> new DatabaseManager(config, null),
                "It should throw null pointer exception!");

        assertThrows(NullPointerException.class,
                () -> new DatabaseManager(null, new ApplicationMock(config)),
                "It should throw null pointer exception!");

        //noinspection ConstantConditions
        assertThrows(NullPointerException.class,
                () -> new DatabaseManager(config, new ApplicationMock(config), null),
                "It should throw null pointer exception!");
    }

    @Test
    void getRepository()
    {
        Repository<TestModel> repo = manager.getRepository(TestModel.class);
        assertNotNull(repo, "Repo should not be null and have not thrown anything");
    }

    @Test
    void getReactiveRepository()
    {
        ReactiveRepository<TestModel> repo = manager.getReactiveRepository(TestModel.class);
        assertNotNull(repo, "Repo should not be null and have not thrown anything");
    }

    @Test
    void createQueryBuilder()
    {
        IQueryObject repo = manager.createQueryBuilder("test");
        assertNotNull(repo, "IQueryObject should not be null");

        assertThrows(NullPointerException.class, () -> manager.createQueryBuilder(null));
    }

    @Test
    void shutdown() {
        IConnection<?> connection = Mockito.spy(manager.getConnection());
        manager.setConnection(connection);
        manager.shutdown();
        Mockito.verify(connection).closeDB();
    }

    @Test
    void getType() {
        assertEquals(DBType.SQLITE, manager.getType(), "Manager should a type of sqlite");
    }

    @Test
    void getQueryObjectClass() {
        assertEquals(DBType.SQLITE.getQueryObjectClass(), manager.getQueryObjectClass(), "Manager should use object class the same as the one for sqlite");
    }

}