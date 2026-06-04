package com.ecommerce.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回体
 * <p>
 * 所有 Controller 接口均返回此类，保证前后端交互格式一致。
 * 前端收到的 JSON 结构：
 * <pre>
 * {
 *     "code": 200,
 *     "msg": "操作成功",
 *     "data": { ... }
 * }
 * </pre>
 *
 * @param <T> data 字段的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {

    /** 状态码：200 成功，其余均视为失败 */
    private Integer code;

    /** 提示消息 */
    private String msg;

    /** 业务数据体 */
    private T data;

    // ==================== 成功 ====================

    /** 无返回数据的成功响应 */
    public static <T> R<T> ok() {
        return new R<>(200, "操作成功", null);
    }

    /** 带返回数据的成功响应 */
    public static <T> R<T> ok(T data) {
        return new R<>(200, "操作成功", data);
    }

    /** 自定义消息的成功响应 */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(200, msg, data);
    }

    // ==================== 失败 ====================

    /** 仅含消息的失败响应（code=500） */
    public static <T> R<T> fail(String msg) {
        return new R<>(500, msg, null);
    }

    /** 自定义状态码和消息的失败响应 */
    public static <T> R<T> fail(Integer code, String msg) {
        return new R<>(code, msg, null);
    }

    /** 带数据的失败响应 */
    public static <T> R<T> fail(Integer code, String msg, T data) {
        return new R<>(code, msg, data);
    }

    // ==================== 便捷判断 ====================

    /** 是否成功 */
    public boolean isSuccess() {
        return code != null && code == 200;
    }
}
