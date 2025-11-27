package com.dianxin.core.api.commands;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.dianxin.core.api.JavaDiscordBot;

public final class CommandContext {
    private final JavaDiscordBot bot;
    private final MessageReceivedEvent event;

    public CommandContext(JavaDiscordBot bot, MessageReceivedEvent event) {
        this.bot = bot;
        this.event = event;
    }

    public JavaDiscordBot getBot() {
        return bot;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public String getAuthorId() {
        return event.getAuthor().getId();
    }

    public String getRaw() {
        return event.getMessage().getContentRaw();
    }

    public void reply(String content) {
        getChannel().sendMessage(content).queue();
    }

    public void replyEphemeral(String content) {
        // for future: ephemeral DM or delete after time. For now just send.
        getChannel().sendMessage(content).queue();
    }
}
