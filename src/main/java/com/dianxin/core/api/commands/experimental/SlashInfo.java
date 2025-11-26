package com.dianxin.core.api.commands.experimental;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Đánh dấu một Slash Command để auto-register.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SlashInfo {
    /** Tên lệnh (ví dụ: "ping") */
    String name();

    /** Mô tả ngắn gọn của lệnh */
    String description() default "Không có mô tả";

    /** Có phải lệnh chỉ dành cho admin không (optional) */
    boolean adminOnly() default false;
}