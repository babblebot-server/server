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

package net.bdavies.db.dialect.obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.obj.ISQLObject;
import net.bdavies.db.query.PreparedQuery;
import net.bdavies.db.query.QueryType;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
@Data
@AllArgsConstructor
public class GroupValue implements ISQLObject
{
    private final ISQLObject object;

    @Override
    public String toSQLString(QueryType type, PreparedQuery query)
    {
        query.setInsideGroup(true);
        String returnVal = " ( " + object.toSQLString(QueryType.NONE, query) + " ) ";
        query.setInsideGroup(false);
        return returnVal;
    }
}
