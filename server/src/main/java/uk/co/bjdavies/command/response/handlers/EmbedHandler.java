package uk.co.bjdavies.command.response.handlers;

import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.FluxProcessor;
import uk.co.bjdavies.api.command.IResponse;
import uk.co.bjdavies.command.ResponseFactory;
import uk.co.bjdavies.command.response.ResponseHandler;

import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public class EmbedHandler extends ResponseHandler {

    public EmbedHandler(Type type, FluxProcessor<IResponse, IResponse> processor) {
        super(type, processor);
    }

    @Override
    protected <T> IResponse getResponse(T o) {
        //noinspection unchecked
        return ResponseFactory.createEmbedResponse((Consumer<EmbedCreateSpec>) o);
    }
}
