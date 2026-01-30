package com.easyarticle.controller;

import com.easyarticle.dto.ApiResponse;
import com.easyarticle.entity.Article;
import com.easyarticle.entity.User;
import com.easyarticle.repository.IUserMapper;
import com.easyarticle.service.ArticleService;
import com.easyarticle.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章控制器
 * 处理文章的增删改查等操作
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final JwtUtil jwtUtil;
    private final IUserMapper iUserMapper;

    /**
     * 从Authorization头中获取用户ID
     * @param authorizationHeader 授权请求头
     * @return Long 用户ID
     */
    private Long getUserIdFromToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // 移除"Bearer "前缀
        String email = jwtUtil.extractUsername(token);
        User user = iUserMapper.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }

    /**
     * 获取用户的所有文章
     * @param authorizationHeader 授权请求头
     * @return ApiResponse 响应对象
     */
    @GetMapping("/list")
    public ApiResponse<List<Article>> getUserArticles(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromToken(authorizationHeader);
            List<Article> articles = articleService.getUserArticles(userId);
            return ApiResponse.success(articles);
        } catch (Exception e) {
            log.error("Error getting user articles: {}", e.getMessage());
            return ApiResponse.fail("Failed to get articles");
        }
    }

    /**
     * 获取单个文章
     * @param id 文章ID
     * @param authorizationHeader 授权请求头
     * @return ApiResponse 响应对象
     */
    @GetMapping("/{id}")
    public ApiResponse<Article> getArticle(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromToken(authorizationHeader);
            Article article = articleService.getUserArticle(userId, id)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));
            return ApiResponse.success(article);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(404, e.getMessage());
        } catch (Exception e) {
            log.error("Error getting article: {}", e.getMessage());
            return ApiResponse.fail("Failed to get article");
        }
    }

    /**
     * 创建文章
     * @param article 文章实体
     * @param authorizationHeader 授权请求头
     * @return ApiResponse 响应对象
     */
    @PostMapping
    public ApiResponse<Article> createArticle(@RequestBody Article article, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromToken(authorizationHeader);
            article.setUserId(userId);
            articleService.createArticle(article);
            return ApiResponse.success(article);
        } catch (Exception e) {
            log.error("Error creating article: {}", e.getMessage());
            return ApiResponse.fail("Failed to create article");
        }
    }

    /**
     * 更新文章
     * @param id 文章ID
     * @param article 文章实体
     * @param authorizationHeader 授权请求头
     * @return ApiResponse 响应对象
     */
    @PutMapping("/update/{id}")
    public ApiResponse<Article> updateArticle(@PathVariable Long id, @RequestBody Article article, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromToken(authorizationHeader);
            // 验证文章是否存在且属于当前用户
            Article existingArticle = articleService.getUserArticle(userId, id)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));
            
            // 更新文章
            existingArticle.setTitle(article.getTitle());
            existingArticle.setContent(article.getContent());
            articleService.updateArticle(existingArticle);
            
            return ApiResponse.success(existingArticle);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(404, e.getMessage());
        } catch (Exception e) {
            log.error("Error updating article: {}", e.getMessage());
            return ApiResponse.fail("Failed to update article");
        }
    }

    /**
     * 删除文章
     * @param id 文章ID
     * @param authorizationHeader 授权请求头
     * @return ApiResponse 响应对象
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteArticle(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromToken(authorizationHeader);
            // 验证文章是否存在且属于当前用户
            Article existingArticle = articleService.getUserArticle(userId, id)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));
            
            // 删除文章
            articleService.deleteArticle(id);
            
            return ApiResponse.success("Article deleted successfully");
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(404, e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting article: {}", e.getMessage());
            return ApiResponse.fail("Failed to delete article");
        }
    }

}
