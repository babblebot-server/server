package uk.co.bjdavies.command;

import discord4j.core.spec.EmbedCreateSpec;
import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.command.IResponse;

import java.util.function.Consumer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
public class ResponseFactory {

    public static IResponse createResponse(String s, Consumer<EmbedCreateSpec> spec) {
        return new IResponse() {
            @Override
            public String getStringResponse() {
                return s;
            }

            @Override
            public Consumer<EmbedCreateSpec> getEmbedCreateSpecResponse() {
                return spec;
            }

            @Override
            public boolean isStringResponse() {

                if (s == null) {
                    log.error("Response is null, please fix!!!");
                    return false;
                }

                return !s.equals("");
            }
        };
    }

    public static IResponse createStringResponse(String s) {
        return createResponse(s, null);
    }

    public static IResponse createEmbedResponse(Consumer<EmbedCreateSpec> s) {
        return createResponse("", s);
    }

}
