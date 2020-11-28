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

package uk.co.bjdavies.db.Table;

import uk.co.bjdavies.api.db.Model;
import uk.co.bjdavies.api.db.fields.IntField;
import uk.co.bjdavies.api.db.fields.PrimaryField;
import uk.co.bjdavies.api.db.fields.StringField;
import uk.co.bjdavies.api.db.fields.Unique;

import java.util.Arrays;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class ModelBlueprint extends Blueprint {

    private final Class<Model> modelClass;

    public ModelBlueprint(Class<Model> modelClass) {
        this.modelClass = modelClass;
    }

    @Override
    public void setup() {
        Arrays.stream(this.modelClass.getDeclaredFields()).forEach(f -> {
            if (f.isAnnotationPresent(IntField.class) && (!f.isAnnotationPresent(PrimaryField.class)
                    || !f.getAnnotation(PrimaryField.class).increments())) {
                IntField field = f.getAnnotation(IntField.class);
                this.integer(f.getName()).defaultValue(field.defaultValue() == -1 ? null : field.defaultValue())
                        .setNullable(field.nullable());
            } else if (f.isAnnotationPresent(StringField.class)) {
                StringField field = f.getAnnotation(StringField.class);
                this.string(f.getName(), field.charLimit())
                        .defaultValue(field.defaultValue().equals("") ? null : field.defaultValue())
                        .setNullable(field.nullable());
            }

            if (f.isAnnotationPresent(PrimaryField.class) && f.isAnnotationPresent(IntField.class)) {
                this.increments(f.getName());
                this.primaryKey(f.getName());
            } else if (f.isAnnotationPresent(PrimaryField.class)) {
                this.primaryKey(f.getName());
            }

            if (f.isAnnotationPresent(Unique.class)) {
                this.uniqueKeys(f.getName());
            }
        });
    }
}
