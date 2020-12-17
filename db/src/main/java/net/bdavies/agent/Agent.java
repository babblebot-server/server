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

package net.bdavies.agent;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class Agent {
    private static final String GENERATED_DATE_PATTERN = "yyyy-MM-dd'T'hh:mm:ss.SSSZZZ";
    private static final Set<ClassLoader> loaders = new HashSet<>();
    private static final List<CtClass> models = new ArrayList<>();
    private static ClassPool cp;
    private static CtClass modelClass = null;
    private static String currentDirectoryPath;

    public static void premain(String args, Instrumentation instrumentation) {
        cp = ClassPool.getDefault();

        try {
            cp.insertClassPath(new ClassClassPath(Class.forName("net.bdavies.db.Model")));
            modelClass = cp.get("net.bdavies.db.Model");
        } catch (ClassNotFoundException | NotFoundException e) {
            e.printStackTrace();
        }
        log.info("Started Babblebot Agent...");

        CtClass finalModelClass = modelClass;
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                try {
                    if (className.contains("Proxy") || className.contains("java/lang") || className.contains("com/sun") || className.contains("Guice") || className.contains("google") || className.contains("GeneratedMethodAccessor") || className.contains("GeneratedConstructorAccessor")) {
                        return null;
                    }
                    if (!loaders.contains(loader) && loader instanceof URLClassLoader) {
                        scanLoader(loader);
                        loaders.add(loader);
                    }

                    CtClass clazz = cp.get(className.replace('/', '.'));

                    if (clazz != null && notAbstract(clazz) && clazz.subclassOf(finalModelClass) &&
                            !clazz.equals(finalModelClass) && !clazz.equals(cp.get("net.bdavies.db.impl.ImplModel"))) {

                        byte[] bytecode = instrument(clazz);
                        log.info("Instrumented model: " + clazz.getName());
                        return bytecode;
                    }

                } catch (Exception e) {
                }
                return null;
            }
        });

    }

    private static void scanLoader(ClassLoader loader) throws Exception {
        System.out.println("Scanning  class loader:  " + loader);
        //lets skip known jars to save some time
        List<String> toSkipList = Arrays.asList("rt.jar", "activejdbc-", "javalite-common", "mysql-connector", "slf4j",
                "rt.jar", "jre", "jdk", "springframework", "servlet-api", "activeweb", "junit", "jackson", "jaxen",
                "dom4j", "guice", "javax", "aopalliance", "commons-logging", "app-config", "freemarker",
                "commons-fileupload", "hamcrest", "commons-fileupload", "commons-io", "javassist", "ehcache", "xml-apis");

        if (loader instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader) loader).getURLs();
            for (URL url : urls) {
                boolean skip = false;
                for (String name : toSkipList) {
                    if (url.getPath().contains(name)) {
                        skip = true;
                    }
                }

                if (!skip) {
                    processURL(url);
                }
            }
        }
    }

    private static void processURL(URL url) throws Exception {
        log.info("Processing: " + url);
        File f = new File(url.toURI());
        if (f.isFile()) {
            processFilePath(f);
        } else {
            processDirectoryPath(f);
        }
    }

    private static void processDirectoryPath(File f) throws Exception {
        currentDirectoryPath = f.getCanonicalPath();
        processDirectory(f);
    }

    private static void processDirectory(File f) throws Exception {
        findFiles(f);
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processDirectory(file);
                }
            }
        }
    }

    private static void findFiles(File f) throws Exception {
        File[] files = f.listFiles((dir, name) -> name.endsWith(".class"));

        if (files != null) {
            for (File file : files) {
                int current = currentDirectoryPath.length();
                String fileName = file.getCanonicalPath().substring(++current);
                String className = fileName.replace(File.separatorChar, '.').substring(0, fileName.length() - 6);
                tryClass(className);
            }
        }
    }

    private static void processFilePath(File file) throws Exception {
        try {
            if (file.getCanonicalPath().toLowerCase().endsWith(".jar")
                    || file.getCanonicalPath().toLowerCase().endsWith(".zip")) {

                ZipFile zip = new ZipFile(file);
                Enumeration<? extends ZipEntry> entries = zip.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();

                    if (entry.getName().endsWith("class")) {
                        InputStream zin = zip.getInputStream(entry);
                        tryClass(entry.getName().replace(File.separatorChar, '.').substring(0, entry.getName().length() - 6));
                        zin.close();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void tryClass(String className) throws Exception {
        try {
            CtClass clazz = cp.get(className.replace('/', '.'));
            if (isModel(clazz)) {
                if (!models.contains(clazz)) {
                    models.add(clazz);
                    System.out.println("Found model: " + className);

                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static boolean isModel(CtClass clazz) {
        return clazz != null && notAbstract(clazz) && clazz.subclassOf(modelClass) && !clazz.equals(modelClass);
    }

    private static byte[] instrument(CtClass target) throws Exception {
        try {
            doInstrument(target);
            target.detach();
            return target.toBytecode();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static void doInstrument(CtClass target) throws NotFoundException, CannotCompileException {
        CtClass modelClass = cp.get("net.bdavies.db.impl.ImplModel");
        target.setSuperclass(modelClass);

        CtMethod[] modelMethods = modelClass.getDeclaredMethods();
        CtMethod[] targetMethods = target.getDeclaredMethods();

        CtMethod modelGetClass = modelClass.getDeclaredMethod("getModelClass");
        CtMethod newGetClass = CtNewMethod.copy(modelGetClass, target, null);
        newGetClass.setBody("{ return " + target.getName() + ".class; }");
        ClassMap classMap = new ClassMap();
        classMap.fix(modelClass);


        CodeConverter conv = new CodeConverter();

        conv.redirectMethodCall(modelGetClass, newGetClass);


        for (CtMethod method : modelMethods) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers)) {
                if (targetHasMethod(targetMethods, method)) {
                    log.info("Detected method: " + method.getName() + ", skipping delegate.");
                } else {
                    CtMethod newMethod;
                    if (Modifier.isProtected(modifiers) || Modifier.isPublic(modifiers)) {
                        newMethod = CtNewMethod.copy(method, target, classMap);
                        newMethod.instrument(conv);
                    } else if ("getModelClass".equals(method.getName())) {
                        newMethod = newGetClass;
                    } else {
                        newMethod = CtNewMethod.delegator(method, target);
                    }

                    // Include the generic signature
                    for (Object attr : method.getMethodInfo().getAttributes()) {
                        if (attr instanceof SignatureAttribute) {
                            newMethod.getMethodInfo().addAttribute((SignatureAttribute) attr);
                        }
                    }
                    addGeneratedAnnotation(newMethod, target);
                    target.addMethod(newMethod);
                }
            }
        }
    }

    private static void addGeneratedAnnotation(CtMethod newMethod, CtClass target) {
        ConstPool constPool = target.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation("javax.annotation.Generated", constPool);
        annot.addMemberValue("value", new StringMemberValue("net.bdavies.agent.Agent", constPool));

        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(GENERATED_DATE_PATTERN);
        annot.addMemberValue("date", new StringMemberValue(now.format(formatter), constPool));

        attr.addAnnotation(annot);
        newMethod.getMethodInfo().addAttribute(attr);
    }

    private static boolean targetHasMethod(CtMethod[] targetMethods, CtMethod method) {
        for (CtMethod targetMethod : targetMethods) {
            if (targetMethod.equals(method)) {
                return true;
            }
        }
        return false;
    }

    private static boolean notAbstract(CtClass clazz) {
        int modifiers = clazz.getModifiers();
        return !(Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers));
    }
}
