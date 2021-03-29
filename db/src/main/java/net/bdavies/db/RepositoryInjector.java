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

package net.bdavies.db;

import com.google.inject.MembersInjector;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
public class RepositoryInjector<I> implements MembersInjector<I>
{
    private final Field field;
    private final InjectRepository repository;
    private final DatabaseManager manager;

    public RepositoryInjector(Field f, DatabaseManager manager)
    {
        this.field = f;
        this.repository = f.getAnnotation(InjectRepository.class);
        this.manager = manager;
        field.setAccessible(true);
    }

    @Override
    public void injectMembers(I instance)
    {
        try
        {
            Class<?> clazz = field.getType();
            if (clazz.equals(Repository.class) || clazz.equals(ReactiveRepository.class))
            {
                Object val = clazz.equals(Repository.class) ? manager
                        .getRepository(repository.value()) : manager.getReactiveRepository(
                        repository.value());
                field.set(instance, val);
            } else
            {
                log.error("Cannot support repository of type: {}! Please use Repository " +
                        "or ReactiveRepository", clazz);
                throw new UnsupportedOperationException(
                        "Cannot support repository! Please use Repository " +
                                "or ReactiveRepository");
            }
        }
        catch (IllegalAccessException e)
        {
            log.error("Illegal access error occurred when setting field: {}, it cannot be private!",
                    field.getName(), e);
        }
    }
}