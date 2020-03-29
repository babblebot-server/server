package uk.co.bjdavies.plugins.importing;

import reactor.core.publisher.Flux;
import uk.co.bjdavies.api.config.IPluginConfig;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public interface IPluginImportStrategy {

    Flux<Object> importPlugin(IPluginConfig config);

}
