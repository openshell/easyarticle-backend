package com.easyarticle.controller;

import com.easyarticle.dto.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器
 * 统一处理所有控制器的异常
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理认证异常
     * @param e BadCredentialsException 认证异常
     * @return ApiResult 响应对象
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public ApiResult<?> handleBadCredentialsException(BadCredentialsException e) {
        log.error("Authentication error: ", e);
        return ApiResult.fail(401, "Invalid email or password");
    }

    /**
     * 处理非法参数异常
     * @param e IllegalArgumentException 非法参数异常
     * @return ApiResult 响应对象
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ApiResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Invalid argument error: ", e);
        return ApiResult.fail(400, e.getMessage());
    }

    /**
     * 处理通用异常
     * @param e Exception 通用异常
     * @return ApiResult 响应对象
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResult<?> handleException(Exception e) {
        log.error("Internal server error: ", e);
        return ApiResult.fail(500, "Internal server error");
    }
}

