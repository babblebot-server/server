package uk.co.bjdavies.config;

import uk.co.bjdavies.api.config.IHttpConfig;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class HttpConfig implements IHttpConfig {

    private int port;

    private int maxWorkerThreads;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getMaxWorkerThreads() {
        return maxWorkerThreads;
    }
}
