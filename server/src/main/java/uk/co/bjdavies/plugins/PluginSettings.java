package uk.co.bjdavies.plugins;

import uk.co.bjdavies.api.plugins.IPluginSettings;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public class PluginSettings implements IPluginSettings {

    private final String name, author, minimumServerVersion, maximumServerVersion;
    private String namespace;

    public PluginSettings(String name, String author, String namespace, String minimumServerVersion, String maximumServerVersion) {
        this.name = name;
        this.author = author;
        this.namespace = namespace;
        this.minimumServerVersion = minimumServerVersion;
        this.maximumServerVersion = maximumServerVersion;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getMinimumServerVersion() {
        return minimumServerVersion;
    }

    @Override
    public String getMaximumServerVersion() {
        return maximumServerVersion;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
