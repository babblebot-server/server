package uk.co.bjdavies.db.Table;

import uk.co.bjdavies.api.db.Model;
import uk.co.bjdavies.api.db.fields.IntField;
import uk.co.bjdavies.api.db.fields.PrimaryField;
import uk.co.bjdavies.api.db.fields.StringField;
import uk.co.bjdavies.api.db.fields.Unique;

import java.util.Arrays;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class ModelBlueprint extends Blueprint {

    private final Class<Model> modelClass;

    public ModelBlueprint(Class<Model> modelClass) {
        this.modelClass = modelClass;
    }

    @Override
    public void setup() {
        Arrays.stream(this.modelClass.getDeclaredFields()).forEach(f -> {
            if (f.isAnnotationPresent(IntField.class) && (!f.isAnnotationPresent(PrimaryField.class)
                    || !f.getAnnotation(PrimaryField.class).increments())) {
                IntField field = f.getAnnotation(IntField.class);
                this.integer(f.getName()).defaultValue(field.defaultValue() == -1 ? null : field.defaultValue())
                        .setNullable(field.nullable());
            } else if (f.isAnnotationPresent(StringField.class)) {
                StringField field = f.getAnnotation(StringField.class);
                this.string(f.getName(), field.charLimit())
                        .defaultValue(field.defaultValue().equals("") ? null : field.defaultValue())
                        .setNullable(field.nullable());
            }

            if (f.isAnnotationPresent(PrimaryField.class) && f.isAnnotationPresent(IntField.class)) {
                this.increments(f.getName());
                this.primaryKey(f.getName());
            } else if (f.isAnnotationPresent(PrimaryField.class)) {
                this.primaryKey(f.getName());
            }

            if (f.isAnnotationPresent(Unique.class)) {
                this.uniqueKeys(f.getName());
            }
        });
    }
}
