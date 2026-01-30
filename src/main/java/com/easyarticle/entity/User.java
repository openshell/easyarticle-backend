package com.easyarticle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 用户实体类
 * 对应数据库中的users表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 创建时间
     */
    private OffsetDateTime createdAt;
    
    /**
     * 更新时间
     */
    private OffsetDateTime updatedAt;

}
