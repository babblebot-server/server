package uk.co.bjdavies.config;

import com.google.gson.Gson;

import java.io.Reader;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: ConfigParser.java
 * Compiled Class Name: ConfigParser.class
 * Date Created: 30/01/2018
 */

public class ConfigParser {
    /**
     * This is the config variable that can be used after parsing through a getter.
     */
    private final Config config;

    /**
     * This is where the config will get parsed.
     *
     * @param json - The inputted file / string.
     */
    public ConfigParser(String json) {
        config = new Gson().fromJson(json, Config.class);
    }

    /**
     * This is where the config will get parsed.
     *
     * @param reader - The reader from which you wish to read the json from.
     */
    public ConfigParser(Reader reader) {
        config = new Gson().fromJson(reader, Config.class);
    }


    /**
     * This will return the Config.
     *
     * @return Config
     */
    public Config getConfig() {
        return config;
    }
}
