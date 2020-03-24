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
            //noinspection ResultOfMethodCallIgnored
            new File("config").mkdirs();
            File f = new File("config/" + json);
            if (!f.exists()) {
                if (f.createNewFile()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ConfigFactory.class
                            .getResourceAsStream("/config.example.json")));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                    writer.write(stringBuilder.toString());
                    writer.flush();
                    writer.close();
                    throw new RuntimeException("You need to insert your bot token into the newly generated config. Exiting now!!");
                } else {
                    throw new RuntimeException("Unable to create config file please refer to the document regarding configuration.");
                }
            }
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader("config/" + json));
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
