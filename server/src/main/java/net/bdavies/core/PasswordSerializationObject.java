package uk.co.bjdavies.core;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.model.IModelProperty;
import net.bdavies.db.model.serialization.ISQLSerializationObject;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class PasswordSerializationObject implements ISQLSerializationObject<Ignore, Password> {
    @Override
    public Password deserialize(Ignore model, String data, IModelProperty property) {
        return new Password(data);
    }

    @Override
    public String serialize(Ignore model, Password data, IModelProperty property) {
        return data != null ? data.getPass() : null;
    }

    @Override
    public String getSQLColumnType() {
        return "varchar";
    }
}
