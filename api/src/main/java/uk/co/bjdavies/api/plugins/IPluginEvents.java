package uk.co.bjdavies.api.plugins;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public interface IPluginEvents {
    /**
     * This will run on a Babblebot-Server Agent HotSwap.
     */
    void onReload();

    /**
     * This will run when the plugin is installed.
     * Runs before the commands are installed, so you can do setup code here.
     */
    void onBoot(IPluginSettings settings);

    /**
     * This will run the plugin is removed from the app or the application has shutdown.
     */
    void onShutdown();
}
