package net.bdavies.db.dialect.connection;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.config.IDatabaseConfig;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class SQLiteConnection extends RDMSConnection {
    public SQLiteConnection(IDatabaseConfig databaseConfig, IApplication application) {
        super(databaseConfig, application);
    }

    @SneakyThrows
    @Override
    protected Connection getConnectionForDB(IDatabaseConfig config, IApplication application) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File(config.getDatabase());
        if (!file.exists()) {
            try {
                if(file.createNewFile()) {
                    log.info("Your DB file has been created at: {}", file.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return DriverManager.getConnection("jdbc:log4jdbc:sqlite:" + config.getDatabase());
    }
}
