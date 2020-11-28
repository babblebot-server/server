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

package uk.co.bjdavies.variables;


import uk.co.bjdavies.api.variables.Variable;

/**
 * BabbleBot, open-source Discord Bot
 * Author: Ben Davies
 * Class Name: GlobalVariables.java
 * Compiled Class Name: GlobalVariables.class
 * Date Created: 30/01/2018
 */

public class GlobalVariables {
    /*
        This class is used to hold all the global variables that you can use when creating custom commands
     */

    @Variable
    public String testVar = "I am just a test variable!";


    /**
     * This allows you get a random GIF image say you want a !randomGIF command for example.
     *
     * @return String
     */
    @Variable
    public String getRandomGIF(Float printMe) {
        return String.valueOf(printMe
                * Math.PI);
    }
}
