package com.example.duidui.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {


//     状态码
    private Integer code;
//     提示信息
    private String msg;
//    返回数据
    private T data;
    /* ================= 成功 ================= */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    /* ================= 失败 ================= */

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}
