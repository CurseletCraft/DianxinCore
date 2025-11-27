package com.dianxin.core.api.commands;

import com.dianxin.core.api.JavaDiscordBot;
import com.dianxin.core.api.commands.annotations.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class CommandManager {

    private final Map<String, RegisteredCommand> commands = new ConcurrentHashMap<>();
    private final String prefix;
    private PermissionHandler permissionHandler = (event, perm) -> perm == null || perm.isBlank(); // allow by default
    // cooldowns: commandName -> (userId -> epochSecondsUntil)
    private final Map<String, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public CommandManager(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
    }

    public void setPermissionHandler(PermissionHandler handler) {
        this.permissionHandler = handler;
    }

    /**
     * Register a command by instance. The class must have @Command.
     */
    public void register(CommandExecutor executor) {
        Command meta = executor.getClass().getAnnotation(Command.class);
        if (meta == null) {
            throw new IllegalArgumentException("Command class must be annotated with @Command");
        }
        register(meta, executor);
    }

    /**
     * Convenient: register by class (will instantiate using no-arg or single-constructor with JavaDiscordBot if available).
     */
    public void register(Class<? extends CommandExecutor> clazz, Object... ctorArgs) {
        Command meta = clazz.getAnnotation(Command.class);
        if (meta == null) throw new IllegalArgumentException("Class not annotated with @Command");
        try {
            CommandExecutor instance = instantiate(clazz, ctorArgs);
            register(meta, instance);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to instantiate command " + clazz.getName(), ex);
        }
    }

    private void register(Command meta, CommandExecutor instance) {
        String name = meta.name().toLowerCase(Locale.ROOT);
        RegisteredCommand rc = new RegisteredCommand(name, meta, instance);
        commands.put(name, rc);
        for (String a : meta.aliases()) commands.put(a.toLowerCase(Locale.ROOT), rc);
    }

    private CommandExecutor instantiate(Class<? extends CommandExecutor> clazz, Object... ctorArgs) throws Exception {
        // try no-arg first
        try {
            Constructor<? extends CommandExecutor> c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (NoSuchMethodException ignored) { }

        // try matching constructor by types
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            ctor.setAccessible(true);
            Class<?>[] params = ctor.getParameterTypes();
            if (params.length != ctorArgs.length) continue;
            boolean ok = true;
            for (int i = 0; i < params.length; i++) {
                if (ctorArgs[i] != null && !params[i].isAssignableFrom(ctorArgs[i].getClass())) {
                    ok = false; break;
                }
            }
            if (!ok) continue;
            return (CommandExecutor) ctor.newInstance(ctorArgs);
        }
        throw new NoSuchMethodException("No suitable constructor found for " + clazz.getName());
    }

    /** Called by your message listener when a message arrives */
    public void dispatch(MessageReceivedEvent event, JavaDiscordBot bot) {
        String raw = event.getMessage().getContentRaw();
        if (!raw.startsWith(prefix)) return;

        String without = raw.substring(prefix.length()).trim();
        if (without.isEmpty()) return;

        String[] parts = without.split("\\s+");
        String name = parts[0].toLowerCase(Locale.ROOT);
        RegisteredCommand rc = commands.get(name);
        if (rc == null) return;

        // permission check
        String perm = rc.meta.permission();
        if (perm != null && !perm.isBlank() && !permissionHandler.hasPermission(event, perm)) {
            event.getChannel().sendMessage("Bạn không có quyền sử dụng lệnh này.").queue();
            return;
        }

        // cooldown check
        if (rc.meta.cooldown() > 0) {
            Map<String, Long> map = cooldowns.computeIfAbsent(rc.name, k -> new ConcurrentHashMap<>());
            String userId = event.getAuthor().getId();
            long until = map.getOrDefault(userId, 0L);
            long now = Instant.now().getEpochSecond();
            if (until > now) {
                long remain = until - now;
                event.getChannel().sendMessage("Lệnh đang cooldown. Vui lòng chờ " + remain + "s").queue();
                return;
            } else {
                map.put(userId, now + rc.meta.cooldown());
            }
        }

        // args
        String[] args = parts.length <= 1 ? new String[0] : Arrays.copyOfRange(parts, 1, parts.length);
        CommandContext ctx = new CommandContext(bot, event);

        try {
            rc.executor.execute(ctx, args);
        } catch (Exception ex) {
            // catch all to avoid uncaught exceptions in listener
            event.getChannel().sendMessage("Đã xảy ra lỗi khi thực thi lệnh.").queue();
            java.util.logging.Logger.getLogger(CommandManager.class.getName()).log(Level.SEVERE, "Command error", ex);
        }
    }

    public Set<String> getRegisteredNames() {
        return new HashSet<>(commands.keySet());
    }

    private static final class RegisteredCommand {
        final String name;
        final Command meta;
        final CommandExecutor executor;

        RegisteredCommand(String name, Command meta, CommandExecutor executor) {
            this.name = name;
            this.meta = meta;
            this.executor = executor;
        }
    }
}
