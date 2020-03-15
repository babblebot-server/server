package uk.co.bjdavies.variables;


import uk.co.bjdavies.api.variables.Variable;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
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
