package uk.co.bjdavies.db.model;

import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.db.Model;
import uk.co.bjdavies.api.db.fields.PrimaryField;
import uk.co.bjdavies.api.db.fields.Property;
import uk.co.bjdavies.api.db.fields.Protected;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
//TODO: Refactor Class follow S.O.L.I.D principles
@Slf4j
public class ImplModel extends Model {

    protected String primaryKey;
    protected static final String M_TABLE_NAME = ModelUtils.getTableName(getModelClass());
    protected String[] protected_fields;
    protected List<ModelProperty> properties = new ArrayList<>();
    private boolean increments = true;
    private String saveType = "create";
    private Map<String, Model> parents;

    protected ImplModel() {
        parents = new HashMap<>();
        //setupFields();
        //ModelUtils.createTable(getClass());
    }

    public static <T extends Model> Set<T> all() {
        return new ModelQueryBuilder<T>(M_TABLE_NAME).get(getModelClass());
    }

    private static <T extends Model> Class<T> getModelClass() {
        throw new UnsupportedOperationException("Not instrumented please install agent.");
    }

    private void setupFields() {
        this.primaryKey = Arrays.stream(this.getClass().getDeclaredFields()).filter(f ->
          f.isAnnotationPresent(PrimaryField.class)).map(Field::getName).findAny().orElse("id");
        this.increments = Arrays.stream(this.getClass().getDeclaredFields()).anyMatch(f ->
          f.isAnnotationPresent(PrimaryField.class) && f.getType().equals(int.class));
        this.protected_fields = Arrays.stream(this.getClass().getDeclaredFields()).filter(f ->
          f.isAnnotationPresent(Protected.class)).map(Field::getName).toArray(String[]::new);

        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Property.class)) {
                Property property = f.getAnnotation(Property.class);
                f.setAccessible(true);
                properties.add(new ModelProperty(property, f, property.dbName().isEmpty() ? f.getName() :
                  property.dbName()));
            }
        }
    }
}
