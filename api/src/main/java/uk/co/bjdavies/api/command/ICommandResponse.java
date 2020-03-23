package uk.co.bjdavies.api.command;


import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public interface ICommandResponse {

    /**
     * This will return an embed response
     * {@link EmbedCreateSpec}
     *
     * @param embed this is the CreateSpec {@link EmbedCreateSpec}
     */
    boolean sendEmbed(Consumer<EmbedCreateSpec> embed);

    /**
     * This will return an embed response
     * {@link EmbedCreateSpec}
     *
     * @param embed this is the CreateSpec {@link EmbedCreateSpec}
     */
    boolean sendEmbed(Mono<Consumer<EmbedCreateSpec>> embed);

    /**
     * This will return an embed response
     * {@link EmbedCreateSpec}
     * <p>
     *
     * @param embed this is the CreateSpec {@link EmbedCreateSpec}
     */
    boolean sendEmbed(Flux<Consumer<EmbedCreateSpec>> embed);

    /**
     * This will send one String and then the command has finished
     *
     * @param string - This is the string you wish to respond with.
     * @return boolean
     */
    boolean sendString(String string);

    /**
     * This is a single reactive string.
     * The command dispatcher will subscribe to this.
     *
     * @param string - mono of a string
     * @return boolean
     */
    boolean sendString(Mono<String> string);

    /**
     * Multiple strings you wish to send to the user.
     * The command dispatcher will subscribe to this.
     * <p>
     *
     * @param string - Flux of string.
     * @return boolean
     */
    boolean sendString(Flux<String> string);

    /**
     * This will handle a response and add it to the response,
     * then make sure it is compatible with command dispatcher if not it will fail
     *
     * @param type - this is the type of response
     * @param obj  - The response
     * @return boolean
     */
    boolean send(Type type, Object obj);

    FluxProcessor<IResponse, IResponse> getResponses();
}
