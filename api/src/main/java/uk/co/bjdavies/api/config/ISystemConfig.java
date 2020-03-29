package uk.co.bjdavies.api.config;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface ISystemConfig {

    /**
     * This will return if the server will auto-update based on github. If turned off the user will manually have to update.
     * Default: On
     *
     * @return boolean
     */
    boolean isAutoUpdateOn();


    /**
     * This will return if debug is on.
     *
     * @return boolean
     */
    boolean isDebugOn();
}
