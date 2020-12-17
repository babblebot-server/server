package net.bdavies.db;

import java.util.Map;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface ICommandBuilder extends IBaseBuilder {

    /**
     * This will insert a row into the table.
     *
     * @param insertValues e.g. "name" => "Ben"
     * @return boolean if the row has been inserted.
     */
    boolean insert(Map<String, Object> insertValues);

    /**
     * This
     *
     * @param updateValues update values that you want to persist to database
     * @return boolean
     */
    boolean update(Map<String, Object> updateValues);

    boolean delete();

    @Override
    ICommandBuilder where(String key, Object value);

    @Override
    ICommandBuilder where(String key, Comparator comparator, Object value);

    @Override
    ICommandBuilder where(WhereStatement statement);

    @Override
    ICommandBuilder and(WhereStatement... statement);

    @Override
    ICommandBuilder or(WhereStatement... statement);

    /**
     * This will build the sql for the command INSERT INTO, UPDATE, DELETE
     * (?, ?, ?) that will get prepared by the connection.
     * MongoDB will just replace ? with the matching index from the List<String>.
     *
     * @return List<String> Prepared Values.
     */
    ISQLCommand buildCommand();
}

