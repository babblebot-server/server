package net.bdavies.db.model.hooks;


import net.bdavies.db.model.IModelQueryBuilder;
import net.bdavies.db.model.Model;
import net.bdavies.db.obj.IQueryObject;

/**
 * This will be called when a model is first created but not when populated from the db
 * <p>&nbsp;</p>
 * <strong>Lifecycle:</strong>
 * <ul>
 *     <li>The fields get setup for db processing</li>
 *     <li>The fields get any data from the DB.create(Model.class, Map <-)</li>
 *     <li>When {@link Model#save()} is used</li>
 *     <li>{@link #onCreate()} is then called</li>
 * </ul>
 * <p>&nbsp;</p>
 * <strong>PLEASE NOTE: </strong> that on a {@link IQueryObject#get(Class)} onCreate will not be called and same with
 * {@link IModelQueryBuilder#all()}
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 * @author <a href="mailto:me@bdavies.net">me@bdavies (Ben Davies)</a>
 */
public interface ICreateHook {
    /**
     * This will be called when you run DB.create(Model.class);
     */
    void onCreate();
}
