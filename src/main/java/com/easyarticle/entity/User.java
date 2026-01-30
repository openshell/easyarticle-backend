package com.easyarticle.entity;

import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 用户实体类
 * 对应数据库中的users表
 */
@Data
public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}
