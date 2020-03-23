package uk.co.bjdavies.api.command;

import discord4j.core.spec.EmbedCreateSpec;

import java.util.function.Consumer;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
public interface IResponse {

    String getStringResponse();

    Consumer<EmbedCreateSpec> getEmbedCreateSpecResponse();

    boolean isStringResponse();
}
