package com.dianxin.core.api.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Deprecated
interface D4Command {
    void handle(SlashCommandInteractionEvent event);
}
