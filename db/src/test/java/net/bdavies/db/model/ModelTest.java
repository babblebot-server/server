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
import net.bdavies.db.DB;
import net.bdavies.db.DBSetup;
import net.bdavies.db.Operator;
import net.bdavies.db.model.serialization.ISQLObjectDeserializer;
import net.bdavies.db.model.serialization.ISQLObjectSerializer;
import net.bdavies.db.query.QueryBuilder;
import org.junit.jupiter.api.Test;
import uk.co.bjdavies.api.IApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v"></a>
 */
@Slf4j
class ModelTest extends DBSetup {
    @Test
    void all() {
        Set<TestModel> models = TestModel.all();
        assertEquals(9, models.size());
    }

    @Test
    void testFailConstruct() {
        assertThrows(UnsupportedOperationException.class, TestModel::new);
        assertThrows(UnsupportedOperationException.class, AnotherTestModel::new);
    }

    @Test
    void testForClassCastException() {
        ClassCastException exception = assertThrows(ClassCastException.class, () -> {
            Set<AnotherTestModel> models = TestModel.find(b -> b.where("text", "Jo"));
            models.forEach(m -> log.error(m.getName()));
        });
        String msg = exception.getMessage();
        boolean contains = msg.contains("cannot be cast to") && msg.contains("AnotherTestModel")
                && msg.contains("TestModel");
        assertTrue(contains);
    }

    @Test
    void create() {
        TestModel testModel = createTestModel("TestData");
        assertEquals("TestData", testModel.getText());
    }

    @Test
    void find() {
        QueryBuilder queryBuilder = new QueryBuilder("test");
        queryBuilder.columns("text").insert(Map.of("text", "FindTest"));
        Set<TestModel> testModels = TestModel.find(b -> b.where("text", "FindTest"));
        assertEquals(1, testModels.size());
        Set<TestModel> testModels1 = TestModel.find(b -> b.where("text", "NotFound!"));
        assertEquals(0, testModels1.size());
        Set<TestModel> testModels2 = TestModel.find(b -> b.where("text", Operator.LIKE, "%J%"));
        assertEquals(3, testModels2.size());
    }

    @Test
    void findOne() {
        Optional<TestModel> testModel = TestModel.findOne(b -> b.where("text", "Jo"));
        assertTrue(testModel.isPresent());
        testModel = TestModel.findOne(b -> b.where("text", "Joline"));
        assertTrue(testModel.isEmpty());
    }

    @Test
    void findLast() {
        Optional<TestModel> testModel = TestModel.findLast(b -> b.where("id", Operator.GT, "0"));
        assertTrue(testModel.isPresent());
        assertEquals("Lex", testModel.get().getText());

        Optional<TestModel> testModel2 = TestModel.findLast(b -> b.where("text", Operator.LIKE, "%Jo%"));
        assertTrue(testModel2.isPresent());
        assertEquals("Joey", testModel2.get().getText());
    }

    @Test
    void deleteAll() {
        boolean t = TestModel.deleteAll();
        assertTrue(t);
        assertEquals(0, TestModel.all().size());
        populateTestTable(9);
        t = TestModel.deleteAll(b -> b.where("text", "Lex"));
        assertTrue(t);
        assertEquals(8, TestModel.all().size());
    }

    @Test
    void serializeObject() {
        Optional<TestModel> testModel = TestModel.findOne(b -> b.where("id", Operator.GT, "1"));
        //noinspection OverlyLongLambda
        testModel.ifPresent(m -> {
            try {
                Method method = m.getClass().getSuperclass()
                        .getDeclaredMethod("serializeObject", Object.class, ModelProperty.class, IApplication.class);
                method.setAccessible(true);
                ISQLObjectSerializer<?, ?> serializer = new HelloWorldSerializer();
                Class<? extends ISQLObjectSerializer<Model, Object>> obj;
                //noinspection unchecked
                obj = (Class<? extends ISQLObjectSerializer<Model,Object>>) serializer.getClass();
                ModelProperty property = new ModelProperty(HelloWorld.class, null, "test",
                        false, false, false, obj, null,
                        null, "", false, Relationship.NONE, false);
                String data = (String) method.invoke(m, new HelloWorld("TestData"), property, application);
                assertEquals("TestData", data);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }

        });
    }

    @Test
    void deserializeObject() {
        Optional<TestModel> testModel = TestModel.findOne(b -> b.where("id", Operator.GT, "1"));
        //noinspection OverlyLongLambda
        testModel.ifPresent(m -> {
            try {
                Method method = m.getClass().getSuperclass().getDeclaredMethod("deSerializeObject", String.class,
                        ModelProperty.class, IApplication.class);
                method.setAccessible(true);
                ISQLObjectDeserializer<?, ?> serializer = new HelloWorldSerializer();
                Class<? extends ISQLObjectDeserializer<Model, Object>> obj;
                //noinspection unchecked
                obj = (Class<? extends ISQLObjectDeserializer<Model,Object>>) serializer.getClass();
                ModelProperty property = new ModelProperty(HelloWorld.class, null, "test",
                        false, false, false, null, obj,
                        null, "", false, Relationship.NONE, false);

                HelloWorld data = (HelloWorld) method.invoke(m, "TestData", property, application);
                assertEquals("TestData", data.getAppender());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                fail();
            }
        });
    }

    @Test
    void reflectFields() {
        TestModel model = createTestModel("TestData");
        assertEquals("TestData", model.getText());
        model.reflectFields(Map.of("text", "TestData2"));
        assertEquals("TestData2", model.getText());
    }

    @Test
    void save() {
        TestModel model = DB.create(TestModel.class, Map.of("text", "TestSave"));
        model.save();
        assertEquals(10, TestModel.all().size());
        assertEquals(10, model.getId());
        Optional<TestModel> testModel = TestModel.findLast(b -> b.where("id", Operator.GT, "0"));
        testModel.ifPresent(m -> {
            assertEquals("TestSave", m.getText());
            assertEquals(10, m.getId());
        });

        model.setText("TestSave2");
        model.save();
        assertEquals("TestSave2", model.getText());
        testModel = TestModel.findLast(b -> b.where("id", Operator.GT, "0"));
        testModel.ifPresent(m -> {
            assertEquals("TestSave2", m.getText());
            assertEquals(10, m.getId());
        });
    }

    @Test
    void delete() {
        Optional<TestModel> testModel = TestModel.findOne(b -> b.where("id", "9"));
        testModel.ifPresent(Model::delete);
        assertEquals(8, TestModel.all().size());

        TestModel testModel1 = DB.create(TestModel.class, Map.of("text", "Lex"));
        testModel1.save();
        assertEquals(9, TestModel.all().size());
        testModel1.delete();
        assertEquals(8, TestModel.all().size());
    }
}