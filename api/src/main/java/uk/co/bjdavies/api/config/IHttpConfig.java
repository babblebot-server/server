package uk.co.bjdavies.api.config;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public interface IHttpConfig {

    /**
     * This will return the port of the httpServer
     *
     * @return String
     */
    int getPort();

    /**
     * This will return how many threads that the http server will handle.
     *
     * @return int
     */
    int getMaxWorkerThreads();
}
