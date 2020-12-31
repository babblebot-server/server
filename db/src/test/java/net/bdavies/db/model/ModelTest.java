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

package net.bdavies.db.model;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.db.DBSetup;
import net.bdavies.db.Operator;
import net.bdavies.db.Repository;
import net.bdavies.db.model.serialization.ISQLObjectDeserializer;
import net.bdavies.db.model.serialization.ISQLObjectSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v"></a>
 */
@Slf4j
class ModelTest extends DBSetup
{

    private Repository<TestModel> repo;

    @BeforeEach
    protected void setUp()
    {
        super.setUp();
        repo = manager.getRepository(TestModel.class);
    }

    @Test
    void init()
    {
        TestModel testModel = new TestModel();
        testModel.init(manager, Map.of("text", "TestData"));
        assertNotNull(testModel);
    }


    @Test
    void serializeObject()
    {
        Optional<TestModel> testModel = repo.findFirst(b -> b.where("id", Operator.GT, "1"));
        //noinspection OverlyLongLambda
        testModel.ifPresent(m -> {
            try
            {
                Method method = m.getClass().getSuperclass()
                        .getDeclaredMethod("serializeObject", Object.class, ModelProperty.class,
                                IApplication.class);
                method.setAccessible(true);
                ISQLObjectSerializer<?, ?> serializer = new HelloWorldSerializationObject();
                Class<? extends ISQLObjectSerializer<Model, Object>> obj;
                //noinspection unchecked
                obj = (Class<? extends ISQLObjectSerializer<Model, Object>>) serializer.getClass();
                ModelProperty property = new ModelProperty(HelloWorld.class, null, "test",
                        false, false, false, obj, null,
                        null, "", false, RelationshipType.NONE, false, false);
                String data = (String) method.invoke(m, new HelloWorld("TestData"), property,
                        manager.getApplication());
                assertEquals("TestData", data);
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
                fail(e.getMessage());
            }

        });
    }

    @Test
    void deserializeObject()
    {
        Optional<TestModel> testModel = repo.findFirst(b -> b.where("id", Operator.GT, "1"));
        //noinspection OverlyLongLambda
        testModel.ifPresent(m -> {
            try
            {
                Method method = m.getClass().getSuperclass()
                        .getDeclaredMethod("deSerializeObject", String.class,
                                ModelProperty.class, IApplication.class);
                method.setAccessible(true);
                ISQLObjectDeserializer<?, ?> serializer = new HelloWorldSerializationObject();
                Class<? extends ISQLObjectDeserializer<Model, Object>> obj;
                //noinspection unchecked
                obj = (Class<? extends ISQLObjectDeserializer<Model, Object>>) serializer.getClass();
                ModelProperty property = new ModelProperty(HelloWorld.class, null, "test",
                        false, false, false, null, obj,
                        null, "", false, RelationshipType.NONE, false, false);

                HelloWorld data = (HelloWorld) method.invoke(m, "TestData", property, manager.getApplication());
                assertEquals("TestData", data.getAppender());
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
                fail();
            }
        });
    }

    @Test
    void reflectFields()
    {
        TestModel model = repo.create(Map.of("text", "TestData"));
        assertEquals("TestData", model.getText());
        model.reflectFields(Map.of("text", "TestData2"));
        assertEquals("TestData2", model.getText());
    }

    @Test
    void save()
    {
        TestModel model = repo.create(Map.of("text", "TestSave"));
        model.save();
        assertEquals(10, repo.getAll().size());
        assertEquals(10, model.getId());
        Optional<TestModel> testModel = repo.findLast(b -> b.where("id", Operator.GT, "0"));
        testModel.ifPresent(m -> {
            assertEquals("TestSave", m.getText());
            assertEquals(10, m.getId());
        });

        model.setText("TestSave2");
        model.save();
        assertEquals("TestSave2", model.getText());
        testModel = repo.findLast(b -> b.where("id", Operator.GT, "0"));
        testModel.ifPresent(m -> {
            assertEquals("TestSave2", m.getText());
            assertEquals(10, m.getId());
        });
    }

    @Test
    void delete()
    {
        Optional<TestModel> testModel = repo.findFirst(b -> b.where("id", "9"));
        testModel.ifPresent(Model::delete);
        assertEquals(8, repo.getAll().size());

        TestModel testModel1 = repo.createAndPersist(Map.of("text", "Lex"));
        assertEquals(9, repo.getAll().size());
        testModel1.delete();
        assertEquals(8, repo.getAll().size());
    }
}
