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

package uk.co.bjdavies.agent;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import javassist.scopedpool.ScopedClassPoolFactory;
import javassist.scopedpool.ScopedClassPoolFactoryImpl;
import javassist.scopedpool.ScopedClassPoolRepositoryImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.db.model.hooks.TimestampUpdateHook;
import net.bdavies.db.model.serialization.DateSerializationObject;
import net.bdavies.db.model.Model;
import net.bdavies.db.model.fields.Property;
import net.bdavies.db.model.ITimestamps;
import net.bdavies.db.model.serialization.UseSerializationObject;
import net.bdavies.db.model.hooks.OnUpdate;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
@Slf4j
public class Agent {

    private static final byte[] BYTES = null;
    private static final String GENERATED_DATE_PATTERN = "yyyy-MM-dd'T'hh:mm:ss.SSSZZZ";
    private static final String MODEL_CLASS = Model.class.getName();
    private static ClassPool cp;
    private static ScopedClassPoolFactory scopedPool;

    private Agent() {}

    public static void premain(String args, Instrumentation instrumentation) {
        cp = ClassPool.getDefault();
        scopedPool = new ScopedClassPoolFactoryImpl();

        log.info("Started Babblebot-DB Agent...");
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                try {

                    ClassPool classPool = scopedPool.create(loader, cp, ScopedClassPoolRepositoryImpl.getInstance());
                    CtClass clazz = classPool.makeClassIfNew(new ByteArrayInputStream(classfileBuffer));

                    if (isModel(clazz)) {
                        byte[] bytecode = instrument(clazz);
                        log.info("Setting up Model: " + clazz.getSimpleName());
                        return bytecode;
                    }

                } catch (Exception e) {
                    log.error("Something went wrong when instrumenting model: {}", classBeingRedefined.getName(), e);
                }
                return BYTES;
            }
        });

    }


    @SneakyThrows
    private static boolean isModel(CtClass clazz) {
        return notAbstract(clazz) && clazz.getSuperclass().getName().equals(MODEL_CLASS)
                && !clazz.getName().equals(MODEL_CLASS)
                && !clazz.getName().equals(Model.class.getName());
    }

    @SneakyThrows
    private static boolean applyTimestamps(CtClass clazz) {
        return Arrays.stream(clazz.getInterfaces()).anyMatch(c -> c.getName().equals(ITimestamps.class.getName()));
    }

    private static byte[] instrument(CtClass target) {
        try {
            doInstrument(target);
            if(applyTimestamps(target)) {
                instrumentTimestamps(target);
            }
            target.detach();
            return target.toBytecode();
        } catch (Exception e) {
            log.error("Error when instrumenting model {}", target.getName(), e);
        }
        return BYTES;
    }

    @SneakyThrows
    private static void instrumentTimestamps(CtClass target) {
        log.info("Applying timestamps to {}", target.getName());
        CodeConverter codeConverter = new CodeConverter();
        CtField createdAt = CtField.make("private java.util.Date createdAt = new java.util.Date();", target);
        addAnnotationsToField(createdAt, target, AnnotationBuilder.getBuilder(Property.class, target), AnnotationBuilder.getBuilder(UseSerializationObject.class, target)
                .addClass("value", DateSerializationObject.class));
        target.addField(createdAt);
        CtMethod createdAtGetter = CtNewMethod.getter("getCreatedAt", createdAt);
        createdAtGetter.instrument(codeConverter);
        addGeneratedAnnotation(createdAtGetter, target);
        target.addMethod(createdAtGetter);
        String src = "private java.util.Date updatedAt = new java.util.Date();";
        CtField updatedAt = CtField.make(src, target);
        addAnnotationsToField(updatedAt, target, AnnotationBuilder.getBuilder(Property.class, target),
                AnnotationBuilder.getBuilder(OnUpdate.class, target).addClass("value", TimestampUpdateHook.class),
                AnnotationBuilder.getBuilder(UseSerializationObject.class, target).addClass("value", DateSerializationObject.class));
        target.addField(updatedAt);
        CtMethod updatedAtGetter = CtNewMethod.getter("getUpdatedAt", updatedAt);
        addGeneratedAnnotation(updatedAtGetter, target);
        updatedAtGetter.instrument(codeConverter);
        target.addMethod(updatedAtGetter);

    }

    private static void doInstrument(CtClass target) throws NotFoundException, CannotCompileException {
        CtClass modelClass = cp.get(Model.class.getName());
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
                    includeSignatureAttribute(method, newMethod);
                    addGeneratedAnnotation(newMethod, target);
                    target.addMethod(newMethod);
                }
            }
        }

    }

    private static void includeSignatureAttribute(CtMethod method, CtMethod newMethod) {
        for (AttributeInfo attr : method.getMethodInfo().getAttributes()) {
            if (attr instanceof SignatureAttribute) {
                newMethod.getMethodInfo().addAttribute(attr);
            }
        }
    }

    private static void addGeneratedAnnotation(CtMethod newMethod, CtClass target) {
        ConstPool constPool = target.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation("javax.annotation.Generated", constPool);
        annotation.addMemberValue("value", new StringMemberValue(Agent.class.getName(), constPool));

        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(GENERATED_DATE_PATTERN);
        annotation.addMemberValue("date", new StringMemberValue(now.format(formatter), constPool));

        attr.addAnnotation(annotation);
        newMethod.getMethodInfo().addAttribute(attr);
    }



    public static void addAnnotationsToField(CtField field, CtClass target, AnnotationBuilder... builder) {
        AnnotationsAttribute attr = new AnnotationsAttribute(target.getClassFile().getConstPool(), AnnotationsAttribute.visibleTag);
        Arrays.stream(builder).map(AnnotationBuilder::build).forEach(attr::addAnnotation);
        field.getFieldInfo().addAttribute(attr);
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
