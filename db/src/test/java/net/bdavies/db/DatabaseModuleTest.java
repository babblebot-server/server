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

import com.google.inject.*;
import lombok.Getter;
import net.bdavies.db.model.TestModel;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
class DatabaseModuleTest extends DBSetup
{

    @Test
    void configure()
    {
        DatabaseConfig config = DatabaseConfig.builder().type("sqlite").database("deleteme.db").build();
        DatabaseModule module = new DatabaseModule(config, new ApplicationMock(config));
        DatabaseManager manager = module.getManager();
        Injector injector = Guice.createInjector(module);

        File file = new File(config.getDatabase());
        assertTrue(file.exists(), "Delete me database file should exist!!");

        assertEquals(manager, injector.getInstance(DatabaseManager.class));

        TestPlugin plugin = injector.getInstance(TestPlugin.class);
        assertNotNull(plugin.getRepo(), "Should make a normal repository");
        assertNotNull(plugin.getReactiveRepository(), "Should make a reactive repository");

        assertThrows(ConfigurationException.class, () -> injector.getInstance(TestFinalFields.class));

        assertThrows(ProvisionException.class,
                () -> injector.getInstance(TestNonSupportedRepo.class));

        manager.shutdown();
        file.delete();
    }

    @Getter
    public final static class TestPlugin {
        @InjectRepository(TestModel.class)
        private Repository<TestModel> repo;

        @InjectRepository(TestModel.class)
        private ReactiveRepository<TestModel> reactiveRepository;
    }

    public final static class TestFinalFields {
        @InjectRepository(TestModel.class)
        private final Repository<TestModel> repo;

        @Inject
        public TestFinalFields(Repository<TestModel> repo) {this.repo = repo;}
    }

    public final static class TestNonSupportedRepo {
        @InjectRepository(TestModel.class)
        private TestModel model;
    }
}