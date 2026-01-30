package com.easyarticle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 文章实体类
 * 对应数据库中的articles表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    /**
     * 文章ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 文章内容
     */
    private String content;
    
    /**
     * 创建时间
     */
    private OffsetDateTime createdAt;
    
    /**
     * 更新时间
     */
    private OffsetDateTime updatedAt;

}
