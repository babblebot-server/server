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

import net.bdavies.db.model.TestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
class RepositoryTest extends DBSetup
{
    private Repository<TestModel> repo;

    @BeforeEach
    protected void setUp()
    {
        super.setUp();
        repo = manager.getRepository(TestModel.class);
    }

    @Test
    void getAll()
    {
        Set<TestModel> testModels = repo.getAll();
        assertEquals(9, testModels.size());
    }

    @Test
    void findFirst()
    {
        Optional<TestModel> john = repo.findFirst(b -> b.where(TestModel::getText, Operator.LIKE, "%Jo%"));
        assertTrue(john.isPresent());
        assertEquals("John", john.get().getText());
    }

    @Test
    void findLast()
    {
        Optional<TestModel> joey = repo.findLast(b -> b.where(TestModel::getText, Operator.LIKE, "%Jo%"));
        assertTrue(joey.isPresent());
        assertEquals("Joey", joey.get().getText());
    }

    @Test
    void find()
    {
        Set<TestModel> jos = repo.find(b -> b.where(TestModel::getText, Operator.LIKE, "%Jo%"));
        assertEquals(3, jos.size());
        assertEquals("John,Jo,Joey", jos.stream().map(TestModel::getText).collect(Collectors.joining(",")));
    }

    @Test
    void create()
    {
        assertNotNull(repo.create());
        TestModel model = repo.create(Map.of("text", "Pete"));
        assertEquals("Pete", model.getText());
    }


    @Test
    void createAndPersist()
    {
        TestModel testModel = repo.createAndPersist(Map.of("text", "Pete"));
        assertEquals("Pete", testModel.getText());
        assertEquals(10, testModel.getId());
        assertEquals(10, repo.getAll().size());
    }

    @Test
    void delete()
    {
        createAndPersist();
        boolean passed = repo.delete(b -> b.where(TestModel::getText, "Pete"));
        assertTrue(passed, "Failed to delete model Pete");
        assertEquals(8, repo.getAll().size());
    }
}