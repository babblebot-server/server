package uk.co.bjdavies.api.db;

import java.util.Set;

/**
 * This is the model class which gets converted at runtime by the babblebot-agent.
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@SuppressWarnings("ClassWithoutLogger")
public abstract class Model {


    /**
     * Construct a Model.
     */
    protected Model() {
    }

    /**
     * This will return a {@link IQueryBuilder} where you can find a model you want or get all of them.
     * {@link IQueryBuilder#get()}
     *
     * @return {@link IQueryBuilder}
     */
    public static <T extends Model> Set<T> all() {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }
}
