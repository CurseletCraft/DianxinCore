package com.dianxin.core.api.commands;

public abstract class AbstractSubCommand extends BaseCommand implements SubcommandRegistry {
    /**
     * Khởi tạo BaseCommand.
     *
     * @param defer        Có tự động defer reply trước khi thực thi không
     * @param guildOnly    Có chỉ cho phép chạy trong guild không
     * @param debugEnabled Có bật debug log không
     */
    public AbstractSubCommand(boolean defer, boolean guildOnly, boolean debugEnabled) {
        super(defer, guildOnly, debugEnabled);
    }
}
