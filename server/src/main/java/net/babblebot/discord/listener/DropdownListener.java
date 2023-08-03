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

package net.babblebot.discord.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.config.IDiscordConfig;
import net.babblebot.api.discord.DiscordMessageSendSpec;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.api.obj.message.discord.interactions.dropdown.DropdownMenu;
import net.babblebot.command.execution.CommandExecutor;
import net.babblebot.discord.obj.factories.DiscordMessageFactory;
import net.babblebot.discord.services.DiscordMessagingService;
import net.babblebot.service.InteractionRegistry;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.30
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class DropdownListener extends ListenerAdapter
{
    private final IDiscordConfig config;
    private final DiscordMessageFactory messageFactory;
    private final DiscordMessagingService messagingService;
    private final CommandExecutor commandExecutor;
    private final InteractionRegistry interactionRegistry;

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event)
    {
        String compId = event.getComponentId();
        val handlerOpt = interactionRegistry.getHandler(compId);
        if (handlerOpt.isPresent())
        {
            DropdownMenu menu = (DropdownMenu) handlerOpt.get();
            if (!menu.isSendResponse())
            {
                event.deferEdit().complete();
            }
            val m = event.getInteraction();
            var guild = m.getGuild();
            var channel = m.getChannel().asTextChannel();
            var author = m.getUser();
            var msgId = m.getIdLong();
            DiscordMessage msg = messageFactory.makeFromInternal(guild, channel, author, msgId,
                    "dropdownSelected");
            InteractionHook hook = null;
            if (menu.isSendResponse())
            {
                hook = event.deferReply().complete();
            }

            DiscordMessageSendSpec spec = menu.getOnResponse().apply(event.getValues().get(0), msg);

            if (menu.isDisableOnSelect())
            {
                interactionRegistry.removeHandler(compId);
                event.getInteraction().editSelectMenu(event.getComponent()
                                .withDisabled(true).createCopy().setDefaultValues(event.getValues().get(0)).build())
                        .complete();
            } else
            {
                event.getInteraction().editSelectMenu(event.getComponent()
                                .createCopy().setDefaultValues(event.getValues().get(0)).build())
                        .complete();
            }

            if (menu.isSendResponse() && hook != null)
            {
                messagingService.sendInteractionFollowup(hook, spec);
            }
        } else
        {
            InteractionHook hook = event.deferReply().complete();
            event.getInteraction().editSelectMenu(event.getComponent()
                            .withDisabled(true))
                    .complete();
            hook.sendMessage("Interaction no longer available").complete();
        }
    }
}
