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

package net.babblebot.command.execution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.babblebot.api.IApplication;
import net.babblebot.api.command.ICommand;
import net.babblebot.api.command.ICommandContext;
import net.babblebot.command.CommandRegistry;
import net.babblebot.command.ResponseFactory;
import net.babblebot.command.errors.UsageException;
import net.babblebot.command.response.BaseResponse;
import org.springframework.stereotype.Service;
import reactor.util.Loggers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Command Executor
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommandExecutor
{
    private final IApplication application;
    private final CommandRegistry commandRegistry;

    public void executeCommand(CommandExecutionSpec spec)
    {
        ICommandContext ctx = spec.getMessageParser().parseString(spec.getMessage());
        boolean canRun = checkMiddleware(ctx);
        if (!canRun)
        {
            return;
        }
        String namespace = commandRegistry.getNamespaceFromCommandName(ctx.getCommandName());
        String commandName = getCleanCommandName(ctx, namespace);
        Optional<ICommand> commandOpt = findCommand(ctx, namespace, commandName);

        if (commandOpt.isEmpty())
        {
            List<ICommand> commandsLike = commandRegistry
                    .getCommandsLike(namespace, commandName, ctx.getType());
            if (commandsLike.isEmpty())
            {
                spec.getCommandRenderer()
                        .render(ResponseFactory.createStringResponse("Could not find command: "
                                + ctx.getCommandName()));
            } else
            {
                renderDidYouMean(commandsLike, spec, ctx, namespace);
            }
            return;
        }

        ICommand command = commandOpt.get();
        boolean isCommandValid = command.validateUsage(ctx);
        if (!isCommandValid)
        {
            spec.getCommandRenderer().onError(new UsageException(command.getUsage()));
            return;
        }

        spec.getOnPreExecution().accept(ctx, command);
        command.exec(application, ctx);
        ctx.getCommandResponse()
                .getResponses()
                .asFlux()
                .cast(BaseResponse.class)
                .doOnComplete(() -> spec.getOnPostExecution().accept(ctx, command))
                .log(Loggers.getLogger("CommandResponse-" + commandName))
                .subscribe(br -> spec.getCommandRenderer().render(br),
                        err -> handleError(err, spec, commandName));
    }

    private void handleError(Throwable err, CommandExecutionSpec spec, String commandName)
    {
        var handled = false;
        if (err instanceof Exception ex)
        {
            handled = spec.getCommandRenderer().onError(ex);
        }

        if (!handled)
        {
            log.error("Unable to render command {}", commandName, err);
        }
    }

    private void renderDidYouMean(List<ICommand> commandsLike, CommandExecutionSpec spec,
                                  ICommandContext ctx, String namespace)
    {
        val sb = new StringBuilder("```markdown\n# Command Not Found\n\nDid You mean?\n");
        commandsLike.forEach(cl -> {
            sb.append(namespace)
                    .append(cl.getAliases()[0])
                    .append("? - ")
                    .append(cl.getDescription())
                    .append("\n");
        });
        sb.append("```");
        spec.getCommandRenderer().render(ResponseFactory.createStringResponse(sb.toString()));
    }

    private Optional<ICommand> findCommand(ICommandContext ctx, String namespace, String commandName)
    {
        return commandRegistry.findCommand(ctx, namespace, commandName);
    }

    private String getCleanCommandName(ICommandContext ctx, String namespace)
    {
        return ctx.getCommandName().replace(namespace, "");
    }

    private boolean checkMiddleware(ICommandContext ctx)
    {
        var canRun = new AtomicBoolean(true);

        commandRegistry.getMiddlewareList().get(null).forEach(m -> {
            if (canRun.get())
            {
                canRun.set(m.onExecute(ctx));
            }
        });

        if (!canRun.get())
        {
            log.info("Cannot run command {} due to failing middleware", ctx.getCommandName());
            return false;
        }


        String namespace = commandRegistry.getNamespaceFromCommandName(ctx.getCommandName());

        commandRegistry.getMiddlewareForNamespace(namespace).doOnNext(middleware -> {
            log.info("Running middleware for: {} while executing command {}", namespace,
                    ctx.getCommandName());
            if (canRun.get())
            {
                canRun.set(middleware.onExecute(ctx));
            }
        }).subscribe();

        if (!canRun.get())
        {
            log.info("Cannot run command due to failing plugin middleware");
            return false;
        }

        return canRun.get();
    }
}
