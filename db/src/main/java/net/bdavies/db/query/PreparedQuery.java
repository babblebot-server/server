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

package net.bdavies.db.query;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.model.Model;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This will hold utility classes for the current query planning on getting executed
 *
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
@Slf4j
@Getter
public class PreparedQuery {
    private final List<String> preparedValues = new LinkedList<>();
    private final List<Predicate<? extends Model>> modelPredicates = new LinkedList<>();
    @Setter
    private boolean insideGroup;

    public void addPreparedValue(String v) {
        preparedValues.add(v);
    }

    private boolean shouldPrepare(String val) {
        return val != null && !NumberUtils.isCreatable(val);
    }
    public String getValueFromString(String val) {
        if(shouldPrepare(val)) {
            addPreparedValue(val);
            return "?";
        }
        return val;
    }
}
