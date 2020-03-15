package uk.co.bjdavies.config;

import lombok.SneakyThrows;
import uk.co.bjdavies.api.config.IConfig;

import java.io.*;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: ConfigFactory.java
 * Compiled Class Name: ConfigFactory.class
 * Date Created: 30/01/2018
 */

public class ConfigFactory {

    /**
     * This factory method will create a config from a json file / string.
     *
     * @param json - The inputted json file / string.
     * @return Config
     */
    @SneakyThrows
    public static IConfig makeConfig(String json) {
        if (json.equals("config.json")) {
            File f = new File(json);
            if (!f.exists()) {
                f.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("{\n  \"discord\": {\n" +
                        "    \"token\": \"Insert me\",\n" +
                        "    \"commandPrefix\": \"!\"\n" +
                        "  },\n" +
                        "  \"database\": {\n" +
                        "    \"type\": \"sqlite\",\n" +
                        "    \"database\": \"Core.db\"\n" +
                        "  }\n}");
                writer.write(stringBuilder.toString());
                writer.flush();
                writer.close();
                throw new RuntimeException("You need to insert your bot token into the newly generated config. Exiting now!!");
            }
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(json));
                return new ConfigParser(bufferedReader).getConfig();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        } else {
            return new ConfigParser(json).getConfig();
        }
        System.out.println("An error occurred when trying to parse the config");
        return null;
    }
}
