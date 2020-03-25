package uk.co.bjdavies.plugins;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class PluginCommandExecutionBuilder {
    private String name;

    private Class<?> pluginClass;

    private Object[] args = new Object[0];

    private Class<?>[] parameterTypes = new Class[0];

    public PluginCommandExecutionBuilder(String name, Class<?> pluginClass) {
        this.name = name;
        this.pluginClass = pluginClass;
    }

    public PluginCommandExecutionBuilder setPluginClass(Class<?> pluginClass) {
        this.pluginClass = pluginClass;
        return this;
    }

    public PluginCommandExecutionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PluginCommandExecutionBuilder setArgs(Object... args) {
        this.args = args;
        return this;
    }

    public PluginCommandExecutionBuilder setParameterTypes(Class<?>... parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    public PluginCommandDefinition build() {
        return new PluginCommandDefinition() {
            public String getName() {
                return PluginCommandExecutionBuilder.this.name;
            }

            public Object[] getArgs() {
                return PluginCommandExecutionBuilder.this.args;
            }

            public Class<?>[] getParameterTypes() {
                return PluginCommandExecutionBuilder.this.parameterTypes;
            }

            public Class<?> getPluginClass() {
                return PluginCommandExecutionBuilder.this.pluginClass;
            }
        };
    }
}
