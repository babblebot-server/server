package net.bdavies.db.model.serialization;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.model.IModelProperty;
import net.bdavies.db.model.Model;

import java.util.Date;

/**
 * This will serialize and deserialize {@link Date} objects
 *
 * @author <a href="mailto:me@bdavies.net">me@bdavies.net (Ben Davies)</a>
 * @since <a href="https://github.com/bendavies99/BabbleBot-Server/releases/tag/v3.0.0">3.0.0</a>
 */
@Slf4j
public class DateSerializationObject implements ISQLSerializationObject<Model, Date> {
    @SneakyThrows
    @Override
    public Date deserialize(Model model, String data, IModelProperty property) {
        return new Date(Long.parseLong(data));
    }

    @Override
    public String serialize(Model model, Date data, IModelProperty property) {
        return String.valueOf(data.getTime());
    }
}
