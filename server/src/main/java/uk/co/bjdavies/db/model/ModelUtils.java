package uk.co.bjdavies.db.model;


import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.db.Model;
import uk.co.bjdavies.api.db.TableName;
import uk.co.bjdavies.api.db.fields.PrimaryField;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class ModelUtils {

    //    private static <T extends Model> IModelBuilder<T> getQueryBuilder(Class<T> clazz) {
    //        //TODO: IF table doesn't exist crash program for migrations.
    //        createTable(clazz);
    //        return new ModelBuilder<>(getPrimaryKey(clazz), getTableName(clazz), DB.getConnection(), clazz);
    //    }
    //
    //    public static <T extends Model> void createTable(Class<T> clazz) {
    //        //noinspection unchecked
    //        DB.createTableIfItDoesntExist(getSchema(getTableName(clazz), (Class<Model>) clazz));
    //    }

    public static <T extends Model> String getTableName(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors()).filter(c -> c.isAnnotationPresent(TableName.class))
          .map(c -> c.getAnnotation(TableName.class).name()).findAny()
          .orElse(pluralize(clazz.getSimpleName())).toLowerCase();
    }

    public static <T extends Model> String getPrimaryKey(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(c -> c.isAnnotationPresent(PrimaryField.class))
          .map(Field::getName).findAny()
          .orElse("id").toLowerCase();
    }


    //    public static <T extends Model> IModelBuilder<T> where(Class<T> clazz, String key, Object value) {
    //        return getQueryBuilder(clazz).where(key, value);
    //    }
    //
    //
    //    public static <T extends Model> IModelBuilder<T> where(Class<T> clazz, String key, Comparator comparator,
    //    Object value) {
    //        return getQueryBuilder(clazz).where(key, comparator, value);
    //    }
    //
    //
    //    public static <T extends Model> IModelBuilder<T> where(Class<T> clazz, WhereStatement statement) {
    //        return getQueryBuilder(clazz).where(statement);
    //    }
    //
    //    public static <T extends Model> Optional<T> find(Class<T> clazz, int id) {
    //        return getQueryBuilder(clazz).find(id);
    //    }
    //
    //    public static <T extends Model> T findOrFail(Class<T> clazz, int id) throws NullPointerException {
    //        return getQueryBuilder(clazz).findOrFail(id);
    //    }
    //
    //
    //    private static TableBuilder getSchema(String tableName, Class<Model> clazz) {
    //        return new TableBuilder(tableName, new ModelBlueprint(clazz));
    //    }

    public static String pluralize(String singular) {
        char lastLetter = singular.charAt(singular.length() - 1);
        switch (lastLetter) {
            case 'y':
                return singular.substring(0, singular.length() - 1) + "ies";
            case 's':
                return singular + "es";
            default:
                return singular + "s";
        }
    }

}
