package uk.co.bjdavies.api.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * This is the connection interface that'll deal with high level abstraction with dealing with a database such as
 * #findOne
 * #findAll
 * #insert
 * #insertGetId
 * #update
 * #delte
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IConnection {

    <T extends IDBRecord> IQueryBuilder<T> createQueryBuilder(String table, String primaryKey);

    ICommandBuilder createCommandBuilder(String tableName);

    <T extends IDBRecord> IQueryBuilder<T> createQueryBuilder(String table, String primaryKey, Class<T> model);

    <T extends IDBRecord> List<T> executeQuery(IQueryBuilder<T> query) throws SQLException;

    Object executeCommand(ICommandBuilder command) throws SQLException;

    Connection getSQLConnection();
}
