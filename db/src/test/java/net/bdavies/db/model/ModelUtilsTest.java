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
import net.bdavies.db.model.fields.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
public class ModelUtilsTest
{

    @Test
    void construct() {
        assertThrows(InvocationTargetException.class, () -> {
            Constructor<ModelUtils> cn = ModelUtils.class.getDeclaredConstructor();
            cn.setAccessible(true);
            cn.newInstance();
        });
    }

    @Test
    void getTableName() {
        assertEquals("test", ModelUtils.getTableName(TestModel.class));
        assertEquals("anothertestmodels", ModelUtils.getTableName(AnotherTestModel.class));
        assertEquals("cmodel", ModelUtils.getTableName(ConstructorModel.class));
    }

    @Test
    void pluralize() {
        assertEquals("tests", ModelUtils.pluralize("test"));
        assertEquals("berries", ModelUtils.pluralize("berry"));
        assertEquals("houses", ModelUtils.pluralize("hous"));
    }

    private static class ConstructorModel extends Model {
        @TableName("cModel")
        public ConstructorModel() {
        }
    }
}
