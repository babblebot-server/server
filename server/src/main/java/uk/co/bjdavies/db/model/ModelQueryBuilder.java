package uk.co.bjdavies.db.model;

import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.db.Model;
import uk.co.bjdavies.db.QueryBuilder;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class ModelQueryBuilder<T extends Model> extends QueryBuilder {

    public ModelQueryBuilder(String table) {
        super(table);
    }
}
