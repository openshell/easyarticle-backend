package com.easyarticle.repository;

import com.easyarticle.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 文章数据访问接口
 * 提供文章相关的数据库操作方法
 */
@Mapper
public interface IArticleMapper {

    /**
     * 插入文章
     * @param article 文章实体
     */
    void insert(Article article);

    /**
     * 根据ID查询文章
     * @param id 文章ID
     * @return Optional<Article> 文章 Optional 对象
     */
    Optional<Article> findById(@Param("id") Long id);

    /**
     * 根据用户ID查询所有文章
     * @param userId 用户ID
     * @return List<Article> 文章列表
     */
    List<Article> findByUserId(@Param("userId") Long userId);

    /**
     * 更新文章
     * @param article 文章实体
     */
    void update(Article article);

    /**
     * 删除文章
     * @param id 文章ID
     */
    void delete(@Param("id") Long id);

    /**
     * 根据用户ID和文章ID查询文章
     * @param userId 用户ID
     * @param id 文章ID
     * @return Optional<Article> 文章 Optional 对象
     */
    Optional<Article> findByUserIdAndId(@Param("userId") Long userId, @Param("id") Long id);

}
