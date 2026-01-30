package com.easyarticle.service;

import com.easyarticle.entity.Article;
import com.easyarticle.repository.IArticleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 文章服务类
 * 提供文章相关的业务逻辑方法
 */
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final IArticleMapper iArticleMapper;

    /**
     * 创建文章
     * @param article 文章实体
     */
    public void createArticle(Article article) {
        iArticleMapper.insert(article);
    }

    /**
     * 获取用户的所有文章
     * @param userId 用户ID
     * @return List<Article> 文章列表
     */
    public List<Article> getUserArticles(Long userId) {
        return iArticleMapper.findByUserId(userId);
    }

    /**
     * 获取单个文章
     * @param id 文章ID
     * @return Optional<Article> 文章 Optional 对象
     */
    public Optional<Article> getArticle(Long id) {
        return iArticleMapper.findById(id);
    }

    /**
     * 获取用户的单个文章
     * @param userId 用户ID
     * @param id 文章ID
     * @return Optional<Article> 文章 Optional 对象
     */
    public Optional<Article> getUserArticle(Long userId, Long id) {
        return iArticleMapper.findByUserIdAndId(userId, id);
    }

    /**
     * 更新文章
     * @param article 文章实体
     */
    public void updateArticle(Article article) {
        iArticleMapper.update(article);
    }

    /**
     * 删除文章
     * @param id 文章ID
     */
    public void deleteArticle(Long id) {
        iArticleMapper.delete(id);
    }

}
