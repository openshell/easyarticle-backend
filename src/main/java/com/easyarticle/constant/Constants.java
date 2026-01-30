package com.easyarticle.constant;

/**
 * 常量类
 * 定义应用中使用的常量，避免魔法值
 */
public class Constants {

    /**
     * JWT相关常量
     */
    public static final class JWT {
        /**
         * Bearer前缀
         */
        public static final String BEARER_PREFIX = "Bearer ";
        
        /**
         * Bearer前缀长度
         */
        public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
        
        /**
         * 用户ID声明键
         */
        public static final String USER_ID_CLAIM = "userId";
    }
    
    /**
     * 响应码常量
     */
    public static final class ResponseCode {
        /**
         * 成功
         */
        public static final int SUCCESS = 1000;
        
        /**
         * 默认失败
         */
        public static final int DEFAULT_ERROR = 5000;
        
        /**
         * 未授权
         */
        public static final int UNAUTHORIZED = 401;
        
        /**
         * 无效参数
         */
        public static final int BAD_REQUEST = 400;
        
        /**
         * 服务器内部错误
         */
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
    
    /**
     * 响应消息常量
     */
    public static final class ResponseMessage {
        /**
         * 成功
         */
        public static final String SUCCESS = "success";
        
        /**
         * 无效的邮箱或密码
         */
        public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
        
        /**
         * 服务器内部错误
         */
        public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    }

}
