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

import net.bdavies.db.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since __RELEASE_VERSION__
 */
@FunctionalInterface
public interface ModelFunction<T extends Model, R> extends Function<T, R>, Serializable
{
   default String getName()
   {
        try {
            Method writeReplace = this.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda sl = (SerializedLambda) writeReplace.invoke(this);
            String[] tokens = sl.getImplClass().split("/");
            if (sl.getImplMethodName().contains("lambda")) {
                throw new UnsupportedOperationException("Method Reference must be from a model");
            }
            return tokens[tokens.length - 1] + "::" + sl.getImplMethodName();
        } catch (Exception e) {
            final Logger logger = LoggerFactory.getLogger(ModelFunction.class);
            logger.error("Error when getting name of method reference", e);
            return ":: ";
        }
    }

   default String getMethodName() {
       return getName().split("::")[1];
   }
}
