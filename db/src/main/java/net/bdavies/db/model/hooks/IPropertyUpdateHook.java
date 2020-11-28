package net.bdavies.db.model.hooks;

import net.bdavies.db.model.Model;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface IPropertyUpdateHook<T extends Model, R> {
    R onUpdate(T model);
}
