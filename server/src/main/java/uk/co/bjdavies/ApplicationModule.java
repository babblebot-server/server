package uk.co.bjdavies;

import com.google.inject.AbstractModule;
import uk.co.bjdavies.api.IApplication;

/**
 * This is a module class that allows to inject application into a class when using {@link com.google.inject.Inject}
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @see Application
 * @since 1.0.0
 */
public class ApplicationModule extends AbstractModule {

    /**
     * Instance of the Application
     */
    private final IApplication application;

    /**
     * Construct a {@link ApplicationModule}
     *
     * @param application instance of {@link IApplication}
     */
    public ApplicationModule(IApplication application) {
        this.application = application;
    }

    /**
     * This configures the module to bind classes to instances
     *
     * @see AbstractModule#bind(Class)
     * @see com.google.inject.binder.AnnotatedBindingBuilder#toInstance(Object)
     */
    @Override
    protected void configure() {
        bind(IApplication.class).toInstance(application);
    }
}
