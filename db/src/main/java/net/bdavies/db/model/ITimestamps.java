package net.bdavies.db.model;

import java.util.Date;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
public interface ITimestamps {
    default Date getCreatedAt() {
        return null;
    }

    default Date getUpdatedAt() {
        return null;
    }
}
