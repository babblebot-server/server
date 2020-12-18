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

package net.bdavies;

import com.google.inject.AbstractModule;
import net.bdavies.api.IApplication;

/**
 * This is a module class that allows to inject application into a class when using {@link com.google.inject.Inject}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @see Application
 * @since 1.0.0
 */
public class ApplicationModule extends AbstractModule {

    /**
     * Instance of the Application
     */
    private final IApplication application;

    /**
     * Construct a {@link ApplicationModule}
     *
     * @param application instance of {@link IApplication}
     */
    public ApplicationModule(IApplication application) {
        this.application = application;
    }

    /**
     * This configures the module to bind classes to instances
     *
     * @see AbstractModule#bind(Class)
     * @see com.google.inject.binder.AnnotatedBindingBuilder#toInstance(Object)
     */
    @Override
    protected void configure() {
        bind(IApplication.class).toInstance(application);
    }
}
