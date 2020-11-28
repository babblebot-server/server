package net.bdavies.db.model.hooks;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.model.Model;

import java.util.Date;

@Slf4j
public class TimestampUpdateHook implements IPropertyUpdateHook<Model, Date> {
    @Override
    public Date onUpdate(Model model) {
        log.info("Model Timestamp update hook called");
        return new Date();
    }
}
