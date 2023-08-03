/*
 * MIT License
 *
 * Copyright (c) 2020 Ben Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.babblebot.discord.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.api.IApplication;
import net.babblebot.api.discord.DiscordMessageSendSpec;
import net.babblebot.api.discord.IDiscordMessagingService;
import net.babblebot.api.obj.message.discord.DiscordChannel;
import net.babblebot.api.obj.message.discord.DiscordGuild;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.api.obj.message.discord.DiscordUser;
import net.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.babblebot.api.obj.message.discord.interactions.button.ButtonType;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownMenu;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownView;
import net.babblebot.discord.DiscordFacade;
import net.babblebot.discord.obj.factories.DiscordChannelFactory;
import net.babblebot.discord.obj.factories.DiscordGuildFactory;
import net.babblebot.discord.obj.factories.DiscordMessageFactory;
import net.babblebot.discord.obj.factories.EmbedMessageFactory;
import net.babblebot.service.InteractionRegistry;
import net.babblebot.variables.VariableParser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Service for sending messages through discord to a channel or a private message
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordMessagingService implements IDiscordMessagingService
{
    private final DiscordFacade facade;
    private final IApplication application;
    private final DiscordGuildFactory discordGuildFactory;
    private final DiscordChannelFactory discordChannelFactory;
    private final DiscordMessageFactory discordMessageFactory;
    private final InteractionRegistry interactionRegistry;

    @Override
    public Optional<DiscordMessage> send(DiscordGuild guild, DiscordChannel channel,
                                         DiscordMessageSendSpec spec)
    {
        return send(guild.getId().toLong(), channel.getId().toLong(), spec);
    }

    @Override
    public Optional<DiscordMessage> send(long guildId, long channelId, DiscordMessageSendSpec spec)
    {
        return wrapReactiveOperation(() -> {
            if (spec.getView() == null)
            {
                Optional<Guild> guildOpt = discordGuildFactory.makeInternalFromId(guildId);
                if (guildOpt.isPresent())
                {
                    Optional<TextChannel> channelOpt = discordChannelFactory.makeInternalFromId(channelId);
                    if (channelOpt.isPresent())
                    {
                        TextChannel channel = channelOpt.get();
                        channel.sendTyping().complete();
                        Message message = channel.sendMessage(getMessageCreateSpec(spec, guildOpt.get()))
                                .complete();

                        return Optional.of(discordMessageFactory.makeFromInternal(message));
                    }
                }
                return Optional.empty();
            } else
            {
                return renderDropdownView(guildId, channelId, spec, null);
            }
        });
    }

    private Optional<DiscordMessage> renderDropdownView(long guildId, long channelId,
                                                        DiscordMessageSendSpec spec, InteractionHook hook)
    {
        AtomicReference<DiscordMessage> viewMessage = new AtomicReference<>(null);
        DropdownView view = spec.getView();
        DropdownMenu menu = view.getMenu().toBuilder()
                .disableOnSelect(false)
                .sendResponse(false)
                .onResponse((s, msg) -> {
                    DiscordMessage vm = viewMessage.get();
                    Message message = discordMessageFactory.makeInternalFromIds(guildId, channelId,
                            vm.getId().toLong()).orElseThrow();
                    viewMessage.set(discordMessageFactory.makeFromInternal(
                            message.editMessage(getMessageEditSpec(view.getOnSelectionView().apply(s, msg),
                                            discordGuildFactory.makeInternalFromId(guildId).orElseThrow()))
                                    .complete()));

                    return DiscordMessageSendSpec.fromString("Showing " + s);
                })
                .build();
        Optional<DiscordMessage> msg;
        if (hook == null)
        {
            msg = send(guildId, channelId, DiscordMessageSendSpec
                    .fromDropdown(menu));
        } else
        {
            msg = sendInteractionFollowup(hook, DiscordMessageSendSpec.fromDropdown(menu));
        }
        String defaultValue = null;
        if (menu.getDefaultValues() != null && !menu.getDefaultValues().isEmpty())
        {
            defaultValue = menu.getDefaultValues().get(0);
        }
        viewMessage.set(send(guildId, channelId, view.getOnSelectionView()
                .apply(defaultValue, null)).orElseThrow());
        return msg;
    }

    private MessageCreateData getMessageCreateSpec(DiscordMessageSendSpec spec, Guild g)
    {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setTTS(spec.isTts());
        List<ActionRow> itemComponents = getActionRows(spec, g, false);
        if (!itemComponents.isEmpty())
        {
            builder.addComponents(itemComponents);
        }
        builder.addEmbeds(getEmbedsFromSpec(spec.getEmbeds(), g));
        builder.addContent(new VariableParser(spec.getContent(), application).toString());
        return builder.build();
    }

    private MessageEditData getMessageEditSpec(DiscordMessageSendSpec spec, Guild g)
    {
        MessageEditBuilder builder = new MessageEditBuilder();
        List<ActionRow> itemComponents = getActionRows(spec, g, false);
        if (!itemComponents.isEmpty())
        {
            builder.setComponents(itemComponents);
        }
        builder.setEmbeds(getEmbedsFromSpec(spec.getEmbeds(), g));
        builder.setContent(new VariableParser(spec.getContent(), application).toString());
        builder.setReplace(true);
        return builder.build();
    }

    private List<ActionRow> getActionRows(DiscordMessageSendSpec spec, Guild g, boolean inline)
    {
        List<ActionRow> rows = new LinkedList<>();
        List<ItemComponent> components = new LinkedList<>();

        spec.getMenus().forEach(ddm -> {
            UUID uuid = UUID.randomUUID();
            StringSelectMenu.Builder builder = StringSelectMenu.create(uuid.toString());
            ddm.getOptions().forEach(ddo -> {
                if (ddo.getDescription() != null && !ddo.getDescription().equals(""))
                {
                    builder.addOption(ddo.getName(), ddo.getValue(), ddo.getDescription());
                } else
                {
                    builder.addOption(ddo.getName(), ddo.getValue());
                }
            });
            builder.setDefaultValues(ddm.getDefaultValues());
            interactionRegistry.addHandler(uuid.toString(), ddm);
            if (!inline)
            {
                rows.add(ActionRow.of(builder.build()));
            } else
            {
                components.add(builder.build());
            }
        });

        spec.getButtons().forEach(button -> {
            UUID uuid = UUID.randomUUID();
            Button impl = switch (button.getType())
            {
                case PRIMARY -> Button.primary(uuid.toString(), button.getLabel());
                case SUCCESS -> Button.success(uuid.toString(), button.getLabel());
                case SECONDARY -> Button.secondary(uuid.toString(), button.getLabel());
                case DANGER -> Button.danger(uuid.toString(), button.getLabel());
                case LINK -> Button.link(button.getLink(), button.getLabel());
            };

            if (button.getType() != ButtonType.LINK)
            {
                interactionRegistry.addHandler(uuid.toString(), button);
            }

            if (!inline)
            {
                rows.add(ActionRow.of(impl));
            } else
            {
                components.add(impl);
            }
        });

        spec.getRow()
                .forEach(row -> rows.addAll(getActionRows(DiscordMessageSendSpec.builder()
                        .menus(row.getMenus()).buttons(row.getButtons()).build(), g, true)));

        if (inline)
        {
            rows.add(ActionRow.of(components));
        }
        return rows;
    }

    private List<MessageEmbed> getEmbedsFromSpec(List<EmbedMessage> embeds, Guild g)
    {
        if (embeds == null)
        {
            return new ArrayList<>();
        }
        return embeds.stream().map(em -> {
            EmbedMessageFactory.addDefaults(em, application, g);
            return EmbedMessageFactory.fromBabblebot(em);
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<DiscordMessage> sendPrivateMessage(DiscordGuild guild, DiscordUser user,
                                                       DiscordMessageSendSpec spec)
    {
        return sendPrivateMessage(guild.getId().toLong(), user.getId().toLong(), spec);
    }

//    @Override
//    public Mono<Void> sendInteractionFollowup(InteractionDiscordMessage message,
//                                              DiscordMessageSendSpec spec)
//    {
//        return wrapReactiveOperation(() -> {
//            Guild g = facade.getClient().getGuildById(Snowflake.of(message.getGuild().getId().toLong()))
//                    .blockOptional().orElseThrow();
//            InteractionFollowupCreateSpec followupSpec = getInteractionCreateSpec(spec, g);
//            val data = followupSpec.asRequest();
//            FollowupMessageRequest newBody = FollowupMessageRequest.builder()
//                    .from(data.getJsonPayload())
//                    .build();
//            return facade.getClient().getApplicationInfo()
//                    .flatMap(appInfo -> facade.getClient().getRestClient().getWebhookService()
//                            .executeWebhook(appInfo.getId().asLong(), message.getToken(), true,
//                                    MultipartRequest.ofRequest(newBody))
//                            .onErrorComplete()
//                            .flatMap(m -> Mono.empty()));
//        });
//    }
//
//    private InteractionFollowupCreateSpec getInteractionCreateSpec(DiscordMessageSendSpec spec, Guild g)
//    {
//        return InteractionFollowupCreateSpec.builder()
//                .tts(spec.isTts())
//                .addAllEmbeds(getEmbedsFromSpec(spec.getEmbeds(), g))
//                .content(spec.getContent())
//                .build();
//    }

    @Override
    public Optional<DiscordMessage> sendPrivateMessage(long guildId, long userId, DiscordMessageSendSpec spec)
    {
        return wrapReactiveOperation(() -> {
            Optional<Guild> guildOpt = discordGuildFactory.makeInternalFromId(guildId);
            if (guildOpt.isPresent())
            {
                Guild guild = guildOpt.get();
                Optional<Member> memberOpt = Optional.ofNullable(guild.getMemberById(userId));
                if (memberOpt.isPresent())
                {
                    Member member = memberOpt.get();
                    PrivateChannel privateChannel = member.getUser().openPrivateChannel().complete();
                    Message message = privateChannel.sendMessage(getMessageCreateSpec(spec, guild))
                            .complete();
                    return Optional.of(discordMessageFactory.makeFromInternal(message));
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public void deleteMessage(DiscordMessage message)
    {
        wrapReactiveOperation(() -> {
            Optional<Message> m = discordMessageFactory.makeInternalFromDiscordMessage(message);
            m.ifPresent(value -> value.delete().complete());
            return null;
        });
    }

    @SneakyThrows
    public <T> T wrapReactiveOperation(Supplier<T> supplier)
    {
        CompletableFuture<T> wrapper = CompletableFuture.supplyAsync(supplier);
        return wrapper.get();
    }

    public Optional<DiscordMessage> sendInteractionFollowup(InteractionHook hook,
                                                            DiscordMessageSendSpec spec)
    {
        if (spec.getView() != null)
        {
            Guild guild = Optional.ofNullable(hook.getInteraction().getGuild()).orElseThrow();
            GuildChannel channel = Optional.of(hook.getInteraction().getGuildChannel()).orElseThrow();
            return renderDropdownView(guild.getIdLong(), channel.getIdLong(), spec, hook);
        }
        return Optional.of(discordMessageFactory.makeFromInternal(hook
                .sendMessage(getMessageCreateSpec(spec, hook.getInteraction().getGuild())).complete()));
    }
}
