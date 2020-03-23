package uk.co.bjdavies.command;

import discord4j.core.spec.EmbedCreateSpec;
import uk.co.bjdavies.api.command.IResponse;

import java.util.function.Consumer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
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
                return s != null && !s.equals("");
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
