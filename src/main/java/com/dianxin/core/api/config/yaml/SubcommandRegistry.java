package com.dianxin.core.api.config.yaml;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public interface SubcommandRegistry {
    SubcommandData getSubcommand();
}
