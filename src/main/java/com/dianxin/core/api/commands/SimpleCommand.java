package com.dianxin.core.api.commands;

import com.dianxin.core.api.DianxinCore;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public abstract class SimpleCommand implements D4Command {
    private final Logger logger;
    private final JDA jda;

    protected SimpleCommand() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.jda = DianxinCore.getJda();
    }

    /**
     * Thực thi logic của command.
     *
     * @param event Sự kiện slash command
     */
    protected abstract void execute(SlashCommandInteractionEvent event);

    /**
     * Yêu cầu quyền cho user (nếu cần).
     * <p>Nếu trả về null → không yêu cầu quyền cụ thể.</p>
     *
     * @return Permission cần có, hoặc {@code null} nếu không yêu cầu
     */
    @NotNull
    protected abstract Collection<Permission> requirePermissions();

    /**
     * <p><b>Example code:</b><br>
     * <pre><code>
     * public class ExampleCommand extends BaseCommand
     * {
     *    {@literal @Override}
     *     public Collection<Permission> requireSelfPermissions() {
     *         return Arrays.asList(Permission.SEND_MESSAGES, Permission.MANAGE_ROLES);
     *     }
     * }
     * </code></pre>
     * @return Self permissions for bot
     */
    @NotNull
    protected abstract Collection<Permission> requireSelfPermissions();

    // Getters

    /**
     * @return Logger của command hiện tại
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * @return Java discord bot chính
     */
    protected JDA getJda() {
        return jda;
    }
}
