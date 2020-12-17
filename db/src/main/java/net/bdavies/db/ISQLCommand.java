package net.bdavies.db;

import java.util.List;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface ISQLCommand {

    String getSQL();

    List<String> getValues();
}
