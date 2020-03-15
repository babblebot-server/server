package uk.co.bjdavies.variables;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: VariableStrategy.java
 * Compiled Class Name: VariableStrategy.class
 * Date Created: 02/02/2018
 */

public interface VariableStrategy {
    String[] parseAllVariables(String toParse);

    String removeTagsFromString(String toParse);
}
