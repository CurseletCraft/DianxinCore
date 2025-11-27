package com.dianxin.core.api.commands;

public interface CommandExecutor {
    /**
     * Called when the command executes.
     * @param ctx context (wraps event, bot, args)
     * @param args separated args (already trimmed)
     */
    void execute(CommandContext ctx, String[] args);
}
