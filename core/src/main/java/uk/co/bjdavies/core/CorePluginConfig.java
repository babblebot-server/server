package uk.co.bjdavies.core;

import lombok.Getter;
import uk.co.bjdavies.api.plugins.PluginConfig;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@PluginConfig.Setup
public class CorePluginConfig {

    @Getter
    private final String test = "Sample data";


    @Getter
    private final int testInt = 1;

    @Getter
    private final float testFloat = 1.1f;

    @Getter
    private final String namespace = "Yoyo";

    @Override
    public String toString() {
        return "CorePluginConfig{" +
                "test='" + test + '\'' +
                ", testInt=" + testInt +
                ", testFloat=" + testFloat +
                ", namespace='" + namespace + '\'' +
                '}';
    }
}
