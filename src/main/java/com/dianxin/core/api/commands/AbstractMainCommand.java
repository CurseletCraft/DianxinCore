package com.dianxin.core.api.commands;

@Deprecated
public abstract class AbstractMainCommand extends BaseCommand implements MaincommandRegistry {
    /**
     * Khởi tạo BaseCommand.
     *
     * @param defer        Có tự động defer reply trước khi thực thi không
     * @param guildOnly    Có chỉ cho phép chạy trong guild không
     * @param debugEnabled Có bật debug log không
     */
    public AbstractMainCommand(boolean defer, boolean guildOnly, boolean debugEnabled) {
        super(defer, guildOnly, debugEnabled);
    }
}
