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

package net.bdavies.command;

import com.google.inject.AbstractModule;
import lombok.Getter;
import net.bdavies.api.command.ICommandDispatcher;

/**
 * This is a module class that allows to inject command stuff into a class when using {@link com.google.inject.Inject}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class CommandModule extends AbstractModule {
    @Getter
    private final ICommandDispatcher commandDispatcher;


    public CommandModule() {
        this.commandDispatcher = new CommandDispatcher();
    }

    @Override
    protected void configure() {
        bind(ICommandDispatcher.class).toInstance(this.commandDispatcher);
    }

}