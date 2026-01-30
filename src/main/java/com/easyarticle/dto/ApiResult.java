package com.easyarticle.dto;

import lombok.Data;

/**
 * API响应统一格式类
 */
@Data
public class ApiResult<T> {

    /**
     * 响应码
     * 1000: 成功
     * 其他: 失败
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ApiResponse 响应对象
     */
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> response = new ApiResult<>();
        response.setCode(1000);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    /**
     * 成功响应（无数据）
     * @param <T> 数据类型
     * @return ApiResponse 响应对象
     */
    public static <T> ApiResult<T> success() {
        return success(null);
    }

    /**
     * 失败响应
     * @param code 响应码
     * @param message 响应消息
     * @param <T> 数据类型
     * @return ApiResponse 响应对象
     */
    public static <T> ApiResult<T> fail(int code, String message) {
        ApiResult<T> response = new ApiResult<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        return response;
    }

    /**
     * 失败响应（默认失败码）
     * @param message 响应消息
     * @param <T> 数据类型
     * @return ApiResponse 响应对象
     */
    public static <T> ApiResult<T> fail(String message) {
        return fail(5000, message);
    }

}
