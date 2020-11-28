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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BabbleBot, open-source Discord Bot
 * Author: Ben Davies
 * Class Name: DollarSignStrategy.java
 * Compiled Class Name: DollarSignStrategy.class
 * Date Created: 02/02/2018
 */

public class DollarSignStrategy implements VariableStrategy {
    @Override
    public String[] parseAllVariables(String toParse) {
        String patternString = "\\$\\(([a-zA-Z0-9()., ]+)\\)";

        Pattern pattern = Pattern.compile(patternString);

        List<String> found = new ArrayList<>();

        Matcher matcher = pattern.matcher(toParse);
        while (matcher.find()) {
            found.add(matcher.group(1));
        }

        return found.toArray(new String[0]);
    }

    @Override
    public String removeTagsFromString(String toParse) {
        String updated = toParse;

        String[] variables = parseAllVariables(toParse);

        for (String var : variables) {
            updated = updated.replace("$(" + var + ")", var);
        }

        return updated;
    }
}
