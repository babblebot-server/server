package uk.co.bjdavies.core;

import lombok.Getter;
import lombok.Setter;
import uk.co.bjdavies.api.db.Model;
import uk.co.bjdavies.api.db.fields.IntField;
import uk.co.bjdavies.api.db.fields.PrimaryField;
import uk.co.bjdavies.api.db.fields.StringField;
import uk.co.bjdavies.api.db.fields.Unique;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public class AnnouncementChannel extends Model {
    @IntField
    @PrimaryField
    @Getter
    private int id;

    @StringField(charLimit = 255)
    @Getter
    @Setter
    @Unique
    private String guildId;

    @StringField(charLimit = 255)
    @Unique
    @Getter
    @Setter
    private String channelId;

    @Override
    public String toString() {
        return "AnnouncementChannel{" +
                "id=" + id +
                ", guildId='" + guildId + '\'' +
                ", channelId='" + channelId + '\'' +
                '}';
    }
}
