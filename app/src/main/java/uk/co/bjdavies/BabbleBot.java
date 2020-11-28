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

package uk.co.bjdavies;

/**
 * A Server for the Discord App this server provides a way for the user to give commands
 * and then this bot acts on those commands
 *
 * @author Ben Davies
 * @see uk.co.bjdavies.Application
 */
public final class BabbleBot {

    /**
     * Constructs a {@code BabbleBot}
     * <p>
     * This constructor is <strong>not</strong> used as this is the entry point for the server.
     */
    private BabbleBot() {
    }

    /**
     * BabbleBot Server Entry point
     *
     * @param args the {@link String} passed in by the command line.
     */
    public static void main(final String[] args) {
        Application.make(BabbleBot.class, args);
    }
}
