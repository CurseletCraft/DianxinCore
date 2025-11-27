package com.dianxin.core.api.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
    String name();
    String[] aliases() default {};
    String description() default "";
    /**
     * A permission string understood by your app (could be role id, permission name, etc).
     * CommandManager uses PermissionHandler to evaluate it.
     */
    String permission() default "";
    /**
     * Cooldown per user in seconds. 0 = no cooldown.
     */
    int cooldown() default 0;
}