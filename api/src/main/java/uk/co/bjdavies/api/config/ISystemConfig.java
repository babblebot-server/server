package uk.co.bjdavies.api.config;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface ISystemConfig {
    /**
     * This will return the JWTTokenPassword.
     *
     * @return String
     */
    String getJWTTokenPassword();

    /**
     * This will return the serverVersion
     *
     * @return String
     */
    String getServerVersion();

    /**
     * This will return if debug is on.
     *
     * @return boolean
     */
    boolean isDebugOn();
}
