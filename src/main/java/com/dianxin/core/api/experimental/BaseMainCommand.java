package com.dianxin.core.api.experimental;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class BaseMainCommand implements MaincommandRegistry {



    @Override
    public abstract CommandData getCommand();
}
