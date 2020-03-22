package uk.co.bjdavies.api.command;


import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    void sendEmbed(Consumer<EmbedCreateSpec> embed);

    /**
     * This will send one String and then the command has finished
     *
     * @param string - This is the string you wish to respond with.
     */
    void sendString(String string);

    /**
     * This is a single reactive string.
     * The command dispatcher will subscribe to this.
     *
     * @param string - mono of a string
     */
    void sendString(Mono<String> string);

    /**
     * Multiple strings you wish to send to the user.
     * The command dispatcher will subscribe to this.
     *
     * @param string - Flux of string.
     */
    void sendString(Flux<String> string);
}
