package uk.co.bjdavies.api.plugins;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public interface IPluginSettings extends IPlugin {

    /**
     * This is the namespace for your commands
     * Please don't use ""
     * <p>
     * Because if it clashes with any commands your commands will not be added to system.
     *
     * @param namespace - the new space you wish to use.
     */
    void setNamespace(String namespace);

}
