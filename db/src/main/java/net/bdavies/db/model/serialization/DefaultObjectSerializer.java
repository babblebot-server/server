package net.bdavies.db.model.serialization;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.model.IModelProperty;
import net.bdavies.db.model.Model;

import javax.inject.Inject;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class DefaultObjectSerializer implements ISQLSerializationObject<Model, Object> {

    @Inject
    public DefaultObjectSerializer() {
    }

    @Override
    public Object deserialize(Model model, String data, IModelProperty property) {
        if (property.getType().equals(int.class)) {
            return Integer.parseInt(data);
        }
        if (property.getType().equals(boolean.class)) {
            return Boolean.parseBoolean(data);
        }
        if (property.getType().equals(float.class)) {
            return Float.parseFloat(data);
        }
        if (property.getType().equals(double.class)) {
            return Double.parseDouble(data);
        }
        if (property.getType().equals(char.class)) {
            return (char) Integer.parseInt(data);
        }
        if (property.getType().equals(short.class)) {
            return (short) Integer.parseInt(data);
        }
        if (property.getType().equals(byte.class)) {
            return (byte) Integer.parseInt(data);
        }
        if (property.getType().equals(String.class)) {
            return data;
        }
        log.error("Type not supported please make a serializer and deserializer for type {} and model {} at property " +
            "{}",
          property.getType(), model.getClass().getSimpleName(), property.getName());
        return null;
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public String serialize(Model model, Object data, IModelProperty property) {
        if(data == null) return null;
        if (property.getType().equals(int.class)) {
            return Integer.toString((int) data);
        }
        if (property.getType().equals(boolean.class)) {
            return ((boolean) data) ? "1" : "0";
        }
        if (property.getType().equals(float.class)) {
            return Float.toString((float) data);
        }
        if (property.getType().equals(double.class)) {
            return Double.toString((double) data);
        }
        if (property.getType().equals(char.class)) {
            return Integer.toString((char) data);
        }
        if (property.getType().equals(short.class)) {
            return Short.toString((short) data);
        }
        if (property.getType().equals(byte.class)) {
            return Byte.toString((byte) data);
        }
        if (property.getType().equals(String.class)) {
            return String.valueOf(data);
        }
        log.error("Type not supported please make a serializer and deserializer for type {} and model {} at property " +
            "{}",
          property.getType(), model.getClass().getSimpleName(), property.getName());
        return null;
    }
}
