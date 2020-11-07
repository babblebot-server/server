package uk.co.bjdavies.db_old;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import uk.co.bjdavies.api.config.IDatabaseConfig;
import uk.co.bjdavies.api.db.ICommandBuilder;
import uk.co.bjdavies.api.db.IConnection;
import uk.co.bjdavies.api.db.IDBRecord;
import uk.co.bjdavies.api.db.IQueryBuilder;
import uk.co.bjdavies.db_old.Table.TableBuilder;

import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Log4j2
public class DB {

    private static DB instance;
    private final ExecutorService service = Executors.newFixedThreadPool(10);
    private volatile IConnection connection;

    private DB(IDatabaseConfig databaseConfig) {
        try {
            connection = ConnectionFactory.make(databaseConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createTableIfItDoesntExist(TableBuilder tableBuilder) {
        if (!getInstance().checkIfTableExists(tableBuilder.getTableName())) {
            getInstance().createTable(tableBuilder);
        }
    }

    @SneakyThrows
    public static <T extends IDBRecord> IQueryBuilder<T> table(String name) {
        return table(name, "id");
    }

    @SneakyThrows
    public static <T extends IDBRecord> IQueryBuilder<T> table(String name, String primaryKey) {
        return getConnection().createQueryBuilder(name, primaryKey);
    }

    public static boolean insert(String tableName, Map<String, Object> values) {
        return getConnection().createCommandBuilder(tableName).insert(values);
    }

    public static ICommandBuilder command(String tableName) {
        return getConnection().createCommandBuilder(tableName);
    }

    public static void install(IDatabaseConfig databaseConfig) {
        if (instance != null) {
            throw new RuntimeException("cannot install DB twice, only one connection can be open at a time for now.");
        }
        else {
            instance = new DB(databaseConfig);
        }
    }

    public static IConnection getConnection() {
        return getInstance().connection;
    }

    private static DB getInstance() {
        if (instance == null) {
            throw new RuntimeException("Database not installed!, please run DB.install(ConnectionOptions); " +
              "Should be ran by the Application Class. Make sure you put Database settings in your config.");
        }

        return instance;
    }

    private boolean checkIfTableExists(String tableName) {
        try {
            Statement statement = connection.getSQLConnection().createStatement();
            statement.executeQuery(String.format("SELECT * FROM %s", tableName));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void createTable(final TableBuilder tableBuilder) {
        try {
            service.submit(() -> {
                Statement preparedStatement;
                preparedStatement = connection.getSQLConnection().createStatement();
                return preparedStatement.execute(tableBuilder.build());
            }).get();
        } catch (Exception e) {
            log.error("Unable to create table:" + tableBuilder.getTableName(), e);
        }
    }
}
