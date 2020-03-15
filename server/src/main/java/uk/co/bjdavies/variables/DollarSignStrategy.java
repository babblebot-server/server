package uk.co.bjdavies.variables;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
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
