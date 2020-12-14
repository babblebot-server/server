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

package net.bdavies.db.obj;

import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.Operator;
import net.bdavies.db.query.PreparedQuery;
import net.bdavies.db.query.QueryLink;
import net.bdavies.db.query.QueryType;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
@Slf4j
@Getter
@AllArgsConstructor
public class WhereStatementObject implements ISQLObject, IMongoObject {
    private final String column;
    private final Operator operator;
    private final String value;
    private final QueryLink link;

    @Override
    public String toSQLString(QueryType type, PreparedQuery query) {
        return " " + (link == null ? "WHERE " : link.name() + " ") + "`" + column + "`" + " " + operator.getOperatorString() + " " +
                query.getValueFromString(value);
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public Bson getFilter() {
        switch (operator) {
            case LT:
                return Filters.lt(column, value);
            case GT:
                return Filters.gt(column, value);
            case LTE:
                return Filters.lte(column, value);
            case GTE:
                return Filters.gte(column, value);
            case NE:
                return Filters.ne(column, value);
            case NIN:
                return Filters.nin(column, Arrays.stream(value.split(",")).map(String::trim)
                        .collect(Collectors.toList()));
            case IN:
                return Filters.in(column, Arrays.stream(value.split(",")).map(String::trim)
                        .collect(Collectors.toList()));
            case NN:
                return Filters.exists(column);
            case LIKE:
                return Filters.regex(column, Pattern.compile(value));
            case EQ:
            default:
                return Filters.eq(column, value);
        }
    }
}
