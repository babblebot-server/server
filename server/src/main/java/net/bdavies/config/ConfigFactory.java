/*
 * MIT License
 *
 * Copyright (c) 2020 Ben Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.bdavies.config;

import lombok.SneakyThrows;
import net.bdavies.api.config.IConfig;

import java.io.*;

/**
 * BabbleBot, open-source Discord Bot
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
