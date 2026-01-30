package com.easyarticle.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 文章响应DTO
 * 用于返回文章信息的响应对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseDTO {

    /**
     * 文章ID
     */
    private Long id;
    
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
