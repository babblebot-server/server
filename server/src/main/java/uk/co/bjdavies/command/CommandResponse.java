package uk.co.bjdavies.command;

import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.bjdavies.api.command.ICommandResponse;

import java.util.function.Consumer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public class CommandResponse implements ICommandResponse {

    @Override
    public void sendEmbed(Consumer<EmbedCreateSpec> embed) {

    }

    @Override
    public void sendString(String string) {

    }

    @Override
    public void sendString(Mono<String> string) {

    }

    @Override
    public void sendString(Flux<String> string) {

    }
}
