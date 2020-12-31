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

import com.mongodb.client.model.Filters;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.DatabaseManager;
import net.bdavies.db.Operator;
import net.bdavies.db.dialect.connection.IConnection;
import net.bdavies.db.dialect.descriptor.ColumnDescriptor;
import net.bdavies.db.dialect.descriptor.MongoDocumentDescriptor;
import net.bdavies.db.model.Model;
import net.bdavies.db.obj.IQueryObject;
import net.bdavies.db.obj.QueryObject;
import net.bdavies.db.obj.WhereStatementObject;
import net.bdavies.db.query.QueryLink;
import net.bdavies.db.query.QueryType;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This is a mongodb query object
 *
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
@Slf4j
@RequiredArgsConstructor
public class MongoDBQueryObject extends QueryObject
{

    @NonNull
    private final String collection;
    @NonNull
    private final IConnection<MongoDocumentDescriptor> connection;
    @NonNull
    private final DatabaseManager manager;

    @Override
    public <T extends Model> Set<T> get(Class<T> clazz)
    {
        return connection.executeQuery(clazz, buildDocumentDescriptor(QueryType.SELECT), null);
    }

    @Override
    public Set<Map<String, String>> get()
    {
        return connection.executeQueryRaw(buildDocumentDescriptor(QueryType.SELECT), null);
    }

    @Override
    public boolean delete()
    {
        return connection.executeCommand(buildDocumentDescriptor(QueryType.DELETE), null);
    }

    @Override
    public boolean insert(Map<String, String> insertValues)
    {
        return connection.executeCommand(buildDocumentDescriptor(QueryType.INSERT), null);
    }

    @Override
    public boolean update(Map<String, String> updateValues)
    {
        return connection.executeCommand(buildDocumentDescriptor(QueryType.UPDATE), null);
    }


    @Override
    public MongoDBQueryObject group(IQueryObject object, QueryLink link)
    {
        wheres.add(new WhereStatementObject("", Operator.NONE,
                ((MongoDBQueryObject) object).buildDocumentDescriptor(QueryType.SELECT).getFilter(), link));
        return this;
    }

    @Override
    public IQueryObject group(Consumer<IQueryObject> builder,
                              QueryLink link)
    {
        IQueryObject object = manager.createQueryBuilder(collection);
        builder.accept(object);
        return group(object, link);
    }

    private MongoDocumentDescriptor buildDocumentDescriptor(QueryType type)
    {
        Bson filter = Filters.exists("_id");
        if (!wheres.isEmpty())
        {
            filter = Filters.and(wheres.stream().map(WhereStatementObject::getFilter)
                    .collect(Collectors.toList()));
        }
        if (columns.isEmpty())
        {
            columns.add("*");
        }
        List<ColumnDescriptor> columnDescriptors = columns.stream()
                .map(c -> new ColumnDescriptor(c, c)).collect(Collectors.toList());

        return new MongoDocumentDescriptor(type, collection, columnDescriptors, filter, values);
    }
}
