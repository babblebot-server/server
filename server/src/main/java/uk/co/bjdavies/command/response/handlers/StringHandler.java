package uk.co.bjdavies.command.response.handlers;

import reactor.core.publisher.FluxProcessor;
import uk.co.bjdavies.api.command.IResponse;
import uk.co.bjdavies.command.ResponseFactory;
import uk.co.bjdavies.command.response.ResponseHandler;

import java.lang.reflect.Type;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public class StringHandler extends ResponseHandler {

    public StringHandler(Type type, FluxProcessor<IResponse, IResponse> processor) {
        super(type, processor);
    }

    @Override
    protected <T> IResponse getResponse(T o) {
        return ResponseFactory.createStringResponse((String) o);
    }
}
