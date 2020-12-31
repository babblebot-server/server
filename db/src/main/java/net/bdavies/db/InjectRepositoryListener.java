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

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@Slf4j
public class InjectRepositoryListener implements TypeListener
{
    private final DatabaseManager manager;
    public InjectRepositoryListener(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter)
    {
       Class<? super I> clazz = type.getRawType();
        Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(InjectRepository.class))
                .forEach(f -> encounter.register(new RepositoryInjector<>(f, manager)));
    }
}
