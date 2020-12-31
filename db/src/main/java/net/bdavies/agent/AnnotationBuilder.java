package net.bdavies.agent;

import javassist.CtClass;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class AnnotationBuilder
{
    private final String className;
    private final Map<String, MemberValue> values = new HashMap<>();
    private final ConstPool constPool;

    private AnnotationBuilder(String n, CtClass ctClass)
    {
        className = n;
        constPool = ctClass.getClassFile().getConstPool();
    }

    public static AnnotationBuilder getBuilder(Class<?> clazz, CtClass ctClass)
    {
        return new AnnotationBuilder(clazz.getName(), ctClass);
    }

    public AnnotationBuilder mapField(String name, MemberValue value)
    {
        values.put(name, value);
        return this;
    }

    public AnnotationBuilder addString(String name, String value)
    {
        values.put(name, new StringMemberValue(value, constPool));
        return this;
    }

    public AnnotationBuilder addClass(String name, Class<?> clazz)
    {
        values.put(name, new ClassMemberValue(clazz.getName(), constPool));
        return this;
    }


    public Annotation build()
    {
        Annotation annotation = new Annotation(className, constPool);
        values.forEach(annotation::addMemberValue);
        return annotation;
    }
}
