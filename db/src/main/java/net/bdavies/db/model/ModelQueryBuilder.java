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
import net.bdavies.db.query.QueryBuilder;

import java.util.Set;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class ModelQueryBuilder<T extends Model> extends QueryBuilder implements IModelQueryBuilder<T> {

    private final Class<T> modelClazz;

    public ModelQueryBuilder(Class<T> modelClazz) {
        super(ModelUtils.getTableName(modelClazz));
        this.modelClazz = modelClazz;
        object.orderBy(ModelUtils.getPrimaryKey(modelClazz));
    }

    @Override
    public ModelQueryBuilder<T> where(String col, String val) {
        object.where(col, val);
        return this;
    }

    @Override
    public ModelQueryBuilder<T> and(String col, String val) {
        object.and(col, val);
        return this;
    }

    @Override
    public ModelQueryBuilder<T> or(String col, String val) {
        object.or(col, val);
        return this;
    }

    @Override
    public ModelQueryBuilder<T> where(String col, String operator, String val) {
        object.where(col, operator, val);
        return this;
    }

    @Override
    public ModelQueryBuilder<T> and(String col, String operator, String val) {
        object.and(col, operator, val);
        return this;
    }

    @Override
    public ModelQueryBuilder<T> or(String col, String operator, String val) {
        object.or(col, operator, val);
        return this;
    }

    public boolean delete() {
        Set<T> models = get(modelClazz);
        models.forEach(Model::delete);
        return true;
    }
}
