package com.easyarticle.dto;

import lombok.Data;

/**
 * 文章请求DTO
 * 用于创建和更新文章的请求对象
 */
@Data
public class ArticleRequestDTO {

    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 文章内容
     */
    private String content;

}
