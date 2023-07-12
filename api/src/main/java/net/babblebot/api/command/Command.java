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

package net.babblebot.api.command;


import net.babblebot.api.plugins.Plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A {@link Command} annotates a {@link Plugin} member function to declare
 * to the {@link ICommandRegistry} to register the function as command to be
 * used when receiving messages from the Message Supplier
 * <p>
 * There is several other annotations that communicate to the {@link ICommandRegistry} how
 * the {@link Command} should look:-
 * <p>
 * {@link CommandExample}
 * {@link CommandParam}
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command
{
    /**
     * An array of aliases that the {@link Command} will use that can be used as
     * shorthands so the user doesn't have to type out the full command
     *
     * @return {@link String}[]
     */
    String[] aliases() default {};

    /**
     * The description for the {@link Command} will be used in the
     * help command provided by the CorePlugin
     *
     * @return {@link String}
     */
    String description() default "";

    /**
     * A {@link Command} that requires a value must present
     * a bit of text outside the parameter context so for example:-
     * <p>
     * !help test
     * <p>
     * "test" would be the value here, you can use this to ensure a command passes in the value
     *
     * @return boolean
     */
    boolean requiresValue() default false;

    /**
     * As explained in {@link #requiresValue()} the value is outside the parameter context and as part
     * of the help command in the CorePlugin it will look at this for an example value for this
     * {@link Command}
     *
     * @return {@link String}
     */
    String exampleValue() default "";

    /**
     * A {@link CommandType} that will determine how this command is handled
     * by the {@link ICommandRegistry}
     *
     * @return {@link CommandType}
     */
    CommandType type() default CommandType.DISCORD;

    /**
     * Send a TTS response for this {@link Command} it will only
     * apply to String responses, it will not work with embed responses
     *
     * @return boolean
     */
    boolean tts() default false;
}
