package uk.co.bjdavies.core;

import lombok.Getter;
import lombok.Setter;
import uk.co.bjdavies.api.db.Model;
import uk.co.bjdavies.api.db.fields.IntField;
import uk.co.bjdavies.api.db.fields.PrimaryField;
import uk.co.bjdavies.api.db.fields.StringField;
import uk.co.bjdavies.api.db.methods.BelongsTo;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class User extends Model {
    @IntField
    @PrimaryField
    @Getter
    private int id;

    @StringField
    @Getter
    @Setter
    private String name;

    @IntField
    @Getter
    @Setter
    private int ignoreId;

    @BelongsTo
    @Getter
    private Ignore ignore;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ignoreId=" + ignoreId +
                '}';
    }
}
