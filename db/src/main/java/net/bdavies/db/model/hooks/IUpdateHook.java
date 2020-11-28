package net.bdavies.db.model.hooks;

import net.bdavies.db.model.Model;

/**
 * The update hook is a {@link Model} hook it will be called when {@link Model#save()} is used
 * unless the model has just been created
 * <p>&nbsp;</p>
 * <strong>Lifecycle:</strong>
 * <ul>
 *     <li>The fields get setup for db processing</li>
 *     <li>The fields get any data from the db query</li>
 *     <li>When {@link Model#save()} is used</li>
 *     <li>{@link #onUpdate()} is then called</li>
 * </ul>
 * <p>&nbsp;</p>
 *
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
public interface IUpdateHook {
    /**
     * This will be called on {@link Model#save()} when a model has been generated from the db
     */
    void onUpdate();
}
