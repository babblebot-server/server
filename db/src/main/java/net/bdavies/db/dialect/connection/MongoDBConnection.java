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

package net.bdavies.db.dialect.connection;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.config.IDatabaseConfig;
import net.bdavies.db.DatabaseManager;
import net.bdavies.db.dialect.descriptor.MongoDocumentDescriptor;
import net.bdavies.db.model.Model;
import net.bdavies.db.query.PreparedQuery;
import org.bson.Document;

import java.lang.reflect.Constructor;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Connection class for the MongoDB Driver
 * <p>
 * Used for MongoDB configurations setup by the user
 *
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
public class MongoDBConnection implements IConnection<MongoDocumentDescriptor>
{

    private final MongoDatabase mongoDBInstance;
    private final MongoClient client;
    protected final DatabaseManager manager;

    /**
     * Construct a MongoDB Connection and setup the underlying MongoDB Client
     *
     * @param manager The database manager in charge of this connection
     */
    public MongoDBConnection(DatabaseManager manager)
    {
        this.manager = manager;
        IDatabaseConfig config = manager.getApplication().getConfig().getDatabaseConfig();
        String authString = "";
        if (config.getUsername() != null && config.getPassword() != null)
        {
            authString = config.getUsername() + ":" + config.getPassword() + "@";
        }
        String connectionStr = "mongodb://" + authString +
                config.getHostname() + ":" + (config.getPort().isEmpty() ? "27017" : config.getPort()) + "/" +
                config.getDatabase();
        client = new MongoClient(new MongoClientURI(connectionStr));
        mongoDBInstance = client.getDatabase(config.getDatabase());
    }


    /**
     * Execute a query and get a Model set back
     *
     * @param clazz         The class to get back
     * @param obj           The query to run {@link String} for SQL, {@link MongoDocumentDescriptor} for
     *                      a Mongo Connection
     * @param preparedQuery The query data
     * @param <T>           Type of class to return
     * @return {@link Set}  collection of Models
     */
    @Override
    public <T extends Model> Set<T> executeQuery(Class<T> clazz, MongoDocumentDescriptor obj,
                                                 PreparedQuery preparedQuery)
    {
        Set<Map<String, String>> values = executeQueryRaw(obj, preparedQuery);
        //noinspection DuplicatedCode
        return values.stream().map(v -> {
            Map<String, Object> vTransform = v.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                    Map.Entry::getValue));
            T model = createModel(clazz);
            model.init(manager, vTransform, true);
            return model;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Helper function to create a model from a class
     *
     * @param clazz The target model class
     * @param <T> The type of model class
     * @return {@link Model} model class
     */
    @SneakyThrows
    private static <T extends Model> T createModel(Class<T> clazz)
    {
        Constructor<? extends T> constructor = clazz.getDeclaredConstructor();
        return constructor.newInstance();
    }


    /**
     * Execute a query and get a Map set back
     *
     * @param obj           The query to run {@link String} for SQL, {@link MongoDocumentDescriptor} for
     *                      a Mongo Connection
     * @param preparedQuery The query data
     * @return {@link Set}  collection of Maps
     */
    @Override
    public Set<Map<String, String>> executeQueryRaw(MongoDocumentDescriptor obj, PreparedQuery preparedQuery)
    {
        Set<Map<String, String>> set = new LinkedHashSet<>();
        Consumer<? super Document> documentConsumer = v -> {
            Map<String, String> newMap;
            newMap = v.entrySet()
                    .stream()
                    .filter(e -> {
                        if ("*".equals(obj.getColumnsToFetch().get(0).getName()))
                        {
                            return true;
                        }
                        return obj.getColumnsToFetch().stream()
                                .anyMatch(cd -> cd.getName().equalsIgnoreCase(e.getKey()));
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));

            set.add(newMap);
        };

        MongoCollection<Document> collection = mongoDBInstance.getCollection(obj.getCollectionName());
        FindIterable<Document> values = collection.find(obj.getFilter());
        values.forEach(documentConsumer);
        return set;
    }

    /**
     * Get the application interface
     *
     * @return {@link IApplication} application interface
     */
    @Override
    public IApplication getApplication()
    {
        return manager.getApplication();
    }

    /**
     * Execute a DB Command
     *
     * @param obj           The query to run {@link String} for SQL, {@link MongoDocumentDescriptor} for
     *                      a Mongo Connection
     * @param preparedQuery The query data
     * @return {@link Boolean} true if successful
     */
    @Override
    public boolean executeCommand(MongoDocumentDescriptor obj, PreparedQuery preparedQuery)
    {
        MongoCollection<Document> collection = mongoDBInstance.getCollection(obj.getCollectionName());
        Map<String, Object> newMap = obj.getValues().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        switch (obj.getType())
        {
            case INSERT:
                try
                {
                    log.info("About to insert Document: {}", newMap);
                    collection.insertOne(new Document(newMap));
                    return true;
                }
                catch (Exception e)
                {
                    log.error("Error when inserting", e);
                    return false;
                }
            case UPDATE:
                try
                {
                    BasicDBObject dbObject = new BasicDBObject();
                    newMap.forEach(dbObject::append);
                    collection.updateOne(obj.getFilter(), dbObject);
                    return true;
                }
                catch (Exception e)
                {
                    log.error("Error when updating", e);
                    return false;
                }
            case DELETE:
                try
                {
                    collection.deleteOne(obj.getFilter());
                    return true;
                }
                catch (Exception e)
                {
                    log.error("Error when deleting", e);
                    return false;
                }
            default:
                return false;
        }
    }

    /**
     * Close the underlying connection to the database
     */
    @Override
    public void closeDB()
    {
        client.close();
    }


}
