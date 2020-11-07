package uk.co.bjdavies.db.language.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.config.IDatabaseConfig;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class SQLiteConnection implements IConnection {

    private final Connection connection;

    @SneakyThrows
    public SQLiteConnection(IDatabaseConfig config) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File(config.getDatabase());
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        connection = DriverManager.getConnection("jdbc:sqlite:" + config.getDatabase());
    }

    @SneakyThrows
    @Override
    public <T> Set<T> executeQuery(Class<T> clazz, String rawSql) {
        return processResultSet(connection.prepareStatement(rawSql).executeQuery(), clazz);
    }

    @Override
    @SneakyThrows
    public Set<Map<String, String>> executeQueryRaw(String rawSql) {
        return processResultSetRaw(connection.prepareStatement(rawSql).executeQuery());
    }

    private <T> Set<T> processResultSet(ResultSet resultSet, Class<T> clazz) {
        Set<Map<String, String>> values = processResultSetRaw(resultSet);
        Gson gson = new GsonBuilder().create();
        return values.stream().map(v -> gson.fromJson(gson.toJsonTree(v), clazz)).collect(Collectors.toSet());
    }

    @SuppressWarnings("MethodWithMultipleLoops")
    private Set<Map<String, String>> processResultSetRaw(ResultSet resultSet) {
        Set<Map<String, String>> set = new LinkedHashSet<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, String> objectMap = new HashMap<>();
                for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(columnName);
                    objectMap.put(columnName, columnValue);
                }

                set.add(objectMap);
            }
        } catch (Exception e) {
            log.error("Error selecting all from the table, most likely the table has not been created.", e);
            return set;
        }
        return set;
    }
}
