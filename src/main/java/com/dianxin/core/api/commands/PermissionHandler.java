package com.dianxin.core.api.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface PermissionHandler {
    /**
     * Return true if the author of the event has the "permission".
     * Implement this to interpret permission strings (role id, role name, "ADMIN", etc).
     */
    boolean hasPermission(MessageReceivedEvent event, String permission);
}
