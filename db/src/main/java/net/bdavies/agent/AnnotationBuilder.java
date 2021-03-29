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

import javassist.CtClass;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder class for making annotations that annotate fields, methods, or classes
 * <p>
 * Used as a fluent api to build annotation that java assist can read
 * <p>
 * {@code Annotation example = AnnotationBuilder.getBuilder(Property.class, target).addClass("value",
 * Object.class).build();}
 *
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
public final class AnnotationBuilder
{
    private final String className;
    private final Map<String, MemberValue> values = new HashMap<>();
    private final ConstPool constPool;

    /**
     * Construct a annotation builder
     *
     * @param n       the name of the class
     * @param ctClass the target class to apply the annotation to
     */
    private AnnotationBuilder(String n, CtClass ctClass)
    {
        className = n;
        constPool = ctClass.getClassFile().getConstPool();
    }

    /**
     * Return a builder from a class
     *
     * @param clazz   the annotation class you wish to add
     * @param ctClass the
     * @return {@link AnnotationBuilder} an instance of an annotation builder
     */
    public static AnnotationBuilder getBuilder(Class<? extends java.lang.annotation.Annotation> clazz,
                                               CtClass ctClass)
    {
        return new AnnotationBuilder(clazz.getName(), ctClass);
    }

    /**
     * Map a field for the annotation
     *
     * @param name  the name of the field
     * @param value the value of the field
     * @return {@link AnnotationBuilder} itself
     */
    public AnnotationBuilder mapField(String name, MemberValue value)
    {
        values.put(name, value);
        return this;
    }

    /**
     * Map a field for the annotation
     *
     * @param name  the name of the field
     * @param value the value of the field
     * @return {@link AnnotationBuilder} itself
     */
    public AnnotationBuilder addString(String name, String value)
    {
        values.put(name, new StringMemberValue(value, constPool));
        return this;
    }

    /**
     * Map a field for the annotation
     *
     * @param name  the name of the field
     * @param clazz the class value of the field
     * @return {@link AnnotationBuilder} itself
     */
    public AnnotationBuilder addClass(String name, Class<?> clazz)
    {
        values.put(name, new ClassMemberValue(clazz.getName(), constPool));
        return this;
    }

    /**
     * Build the java assist annotation from the mapped fields
     *
     * @return {@link Annotation} built annotation
     */
    public Annotation build()
    {
        Annotation annotation = new Annotation(className, constPool);
        values.forEach(annotation::addMemberValue);
        return annotation;
    }
}
