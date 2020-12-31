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

package net.bdavies.core;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import reactor.core.publisher.Mono;
import net.bdavies.api.IApplication;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
public class UpdateService {

    private final IApplication application;

    @Inject
    public UpdateService(IApplication application) {
        this.application = application;
    }

    public Mono<Void> updateTo(AnnouncementService.TagItem tag) {
        return Mono.create(sink -> {
            try {
                File tmp = File.createTempFile("update", "bb");
                File unZipTmp = new File("tmp/updates/" + tag.getTag_name().toLowerCase(Locale.ROOT).replace("v", ""));
                FileUtils.copyURLToFile(new URL(tag.getAssets().get(1).getBrowser_download_url()), tmp);
                if (!unZipTmp.exists()) {
                    unZipTmp.mkdirs();
                }
                File firstDir = null;
                ZipInputStream zis = new ZipInputStream(new FileInputStream(tmp));
                ZipEntry entry = zis.getNextEntry();
                while (entry != null) {
                    String filePath = unZipTmp + File.separator + entry.getName();
                    if (!entry.isDirectory()) {
                        // if the entry is a file, extracts it
                        extractFile(zis, filePath);
                    } else {
                        // if the entry is a directory, make the directory
                        File dir = new File(filePath);
                        dir.mkdir();
                        if (firstDir == null)
                            firstDir = dir;
                    }
                    zis.closeEntry();
                    entry = zis.getNextEntry();
                }
                zis.close();
                if (swapLibs(firstDir, tag.getTag_name().toLowerCase(Locale.ROOT).replace("v", ""))) {
                    if (swapScripts(firstDir)) {
                        unZipTmp.deleteOnExit();
                        sink.success();
                    } else {
                        sink.error(new Exception("Something went wrong.."));
                    }
                } else {
                    sink.error(new Exception("Something went wrong.."));
                }
            } catch (IOException e) {
                sink.error(e);
            }
        });
    }

    private boolean swapScripts(File unZipTmp) throws IOException {
        File unzippedApp = new File(unZipTmp + File.separator + "bin/Babblebot");
        log.info(unzippedApp.getAbsolutePath());
        File appSh = new File("Babblebot");

        if (!unzippedApp.exists()) {
            if (!unzippedApp.createNewFile()) {
                log.error("No APP file!!!");
                return false;
            }
        }

        if (!appSh.exists()) {
            if (!appSh.createNewFile()) {
                log.error("No APP file!!!");
                return false;
            }
        }

        if (appSh.delete()) {
            try {
                FileUtils.copyFile(unzippedApp, appSh);
                if (!appSh.setExecutable(true)) {
                    log.error("Cannot set file to executable..");
                    return false;
                }
            } catch (IOException e) {
                log.error("Cant copy", e);
                return false;
            }
        }

        return true;
    }

    private boolean swapLibs(File unZipTmp, String version) {
        File libsFolder = new File(unZipTmp + File.separator + "lib/");
        File ourLibsFolder = new File("../lib/");
        log.info("Lib folder: " + ourLibsFolder.getAbsolutePath());

        if (!ourLibsFolder.exists()) {
            if (!ourLibsFolder.mkdirs()) {
                log.error("No Lib folder!!!");
                return false;
            }
        }

        if (!libsFolder.exists()) {
            if (!libsFolder.mkdirs()) {
                log.error("No Lib folder!!!");
                return false;
            }
        }
        AtomicBoolean copy = new AtomicBoolean(true);
        try {
            Arrays.stream(Objects.requireNonNull(ourLibsFolder.listFiles()))
                    .filter(f -> (f.getName().toLowerCase(Locale.ROOT).contains("babblebot") &&
                            !f.getName().toLowerCase(Locale.ROOT).contains(version)) /* Delete old babblebot jars */ ||
                            Arrays.stream(Objects.requireNonNull(libsFolder.listFiles()))
                                    .noneMatch(f1 -> f1.getName().equals(f.getName())))
                    /* delete outdated libraries */
                    .forEach(File::deleteOnExit);
            Arrays.stream(Objects.requireNonNull(libsFolder.listFiles())).forEach(f -> {
                try {
                    FileUtils.copyFileToDirectory(f, ourLibsFolder);
                } catch (IOException e) {
                    log.error("Cant copy", e);
                    copy.set(false);
                }
            });
        } catch (NullPointerException e) {
            log.error("Cant copy", e);
            return false;
        }


        return copy.get();
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

}
