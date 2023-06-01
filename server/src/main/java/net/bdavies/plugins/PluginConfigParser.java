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

package net.bdavies.plugins;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.plugins.ICustomPluginConfig;
import net.bdavies.api.plugins.IPlugin;
import net.bdavies.api.plugins.PluginConfig;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
public class PluginConfigParser
{

    public static void parsePlugin(IApplication application, IPlugin settings, Object plugin)
    {
        Field[] fields = plugin.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            if (field.isAnnotationPresent(PluginConfig.class))
            {
                PluginConfig pluginConfig = field.getAnnotationsByType(PluginConfig.class)[0];
                Class<?> clazz;
                if (pluginConfig.value() != ICustomPluginConfig.class)
                {
                    clazz = pluginConfig.value();
                } else
                {
                    clazz = field.getType();
                }

                log.info("Parsing Config class: " + clazz.getSimpleName());

                String fileName = "";
                boolean autoGenerate = true;

                if (clazz.isAnnotationPresent(PluginConfig.Setup.class))
                {
                    //Then handle a setup class
                    PluginConfig.Setup setup = clazz.getAnnotationsByType(PluginConfig.Setup.class)[0];
                    fileName = setup.fileName().equals("") ? settings.getName() : setup.fileName();
                    autoGenerate = setup.autoGenerate();
                } else
                {
                    //Handle a interface
                    try
                    {
                        ICustomPluginConfig customPluginConfig = (ICustomPluginConfig) application.get(clazz);
                        autoGenerate = customPluginConfig.autoGenerateConfig();
                        fileName = customPluginConfig.fileName();
                    }
                    catch (ClassCastException exception)
                    {
                        log.error(
                                "Your config field does meet the type requirements it must either use " +
                                        "PluginConfig.Setup or implement ICustomPluginConfig...",
                                exception);
                        return;
                    }

                }

                //noinspection ResultOfMethodCallIgnored
                new File("config").mkdirs();
                File file = new File("config/" + fileName + ".config.json");
                if (!file.exists())
                {
                    try
                    {
                        if (file.createNewFile())
                        {
                            if (autoGenerate)
                            {
                                FileWriter writer = new FileWriter(file);
                                writer.write(
                                        new ObjectMapper().setDefaultPrettyPrinter(new DefaultPrettyPrinter())
                                                .writeValueAsString(application.get(clazz)));
                                writer.flush();
                                writer.close();
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }

                try
                {
                    FileReader reader = new FileReader(file);

                    BufferedReader bufferedReader = new BufferedReader(reader);

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(line).append("\n");
                    }


                    field.setAccessible(true);

                    field.set(plugin, new ObjectMapper().readValue(stringBuilder.toString(), clazz));
                }
                catch (IOException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

}
