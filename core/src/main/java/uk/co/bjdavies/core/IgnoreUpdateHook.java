package uk.co.bjdavies.core;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.model.hooks.IPropertyUpdateHook;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class IgnoreUpdateHook implements IPropertyUpdateHook<Ignore, String> {
    @Override
    public String onUpdate(Ignore model) {
        return "Updated! Through Hook";
    }
}
