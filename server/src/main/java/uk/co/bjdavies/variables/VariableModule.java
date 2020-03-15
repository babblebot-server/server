package uk.co.bjdavies.variables;

import com.google.inject.AbstractModule;
import lombok.Getter;
import uk.co.bjdavies.api.variables.IVariableContainer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class VariableModule extends AbstractModule {

    @Getter
    private final IVariableContainer container;


    public VariableModule() {
        container = new VariableContainer();
        container.addAllFrom(GlobalVariables.class);
    }

    @Override
    protected void configure() {
        bind(IVariableContainer.class).toInstance(this.container);
    }
}
