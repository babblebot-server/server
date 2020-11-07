package uk.co.bjdavies.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.db.fields.Property;

import java.lang.reflect.Field;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@AllArgsConstructor
@Getter
@Slf4j
@ToString
public class ModelProperty {
    private final Property property;
    private final Field field;
    private final String name;
}
