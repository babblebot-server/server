package uk.co.bjdavies.command.response;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;
import uk.co.bjdavies.api.command.IResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public abstract class ResponseHandler {

    private final FluxProcessor<IResponse, IResponse> processor;
    private final Type type;


    protected ResponseHandler(Type type, FluxProcessor<IResponse, IResponse> processor) {
        this.processor = processor;
        this.type = type;

    }

    public void handle(Object o) {
        if (isAFlux()) {
            //noinspection unchecked
            handleFlux(((Flux<Object>) o).map(this::getResponse));
        } else if (isAMono()) {
            //noinspection unchecked
            handleMono(((Mono<Object>) o).map(this::getResponse));
        } else {
            handleBase(getResponse(o));
        }
    }

    protected abstract <T> IResponse getResponse(T o);

    private boolean isParamType() {
        return type instanceof ParameterizedType;
    }

    private boolean isAFlux() {
        if (isParamType()) {
            return ((ParameterizedType) type).getRawType() == Flux.class;
        }
        return false;
    }

    private boolean isAMono() {
        if (isParamType()) {
            return ((ParameterizedType) type).getRawType() == Mono.class;
        }
        return false;
    }

    private void handleFlux(Flux<IResponse> responses) {
        responses.subscribe(processor::onNext, null, processor::onComplete);
    }

    private void handleMono(Mono<IResponse> response) {
        response.subscribe(processor::onNext, null, processor::onComplete);
    }

    private void handleBase(IResponse response) {
        processor.onNext(response);
        processor.onComplete();
    }
}
