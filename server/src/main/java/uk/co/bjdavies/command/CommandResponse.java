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
    public boolean sendEmbed(Consumer<EmbedCreateSpec> embed) {
        return false;
    }

    @Override
    public boolean sendString(String string) {
        return false;
    }

    @Override
    public boolean sendString(Mono<String> string) {
        return false;
    }

    @Override
    public boolean sendString(Flux<String> string) {
        return false;
    }

    @Override
    public boolean send(Object obj) {
        return false;
    }
}
