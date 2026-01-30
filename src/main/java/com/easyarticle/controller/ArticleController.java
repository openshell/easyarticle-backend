package com.easyarticle.controller;

import com.easyarticle.dto.ApiResult;
import com.easyarticle.entity.Article;
import com.easyarticle.entity.User;
import com.easyarticle.repository.IUserMapper;
import com.easyarticle.service.ArticleService;
import com.easyarticle.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "文章管理", description = "文章的增删改查等操作")
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
    @Operation(summary = "获取用户的所有文章", description = "获取当前登录用户的所有文章列表")
    @ApiResponses(value = {
             @ApiResponse(responseCode  = "200", description ="成功"),
             @ApiResponse(responseCode  = "404", description ="未找到用户"),
             @ApiResponse(responseCode  = "500", description ="服务器内部错误")
    })
    @GetMapping("/list")
    public ApiResult<List<Article>> getUserArticles(@Parameter(description = "授权请求头，格式为Bearer token", required = true) @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromToken(authorizationHeader);
            List<Article> articles = articleService.getUserArticles(userId);
            return ApiResult.success(articles);
        } catch (Exception e) {
            log.error("Error getting user articles: {}", e.getMessage());
            return ApiResult.fail("Failed to get articles");
        }
    }

    /**
     * 获取单个文章
     * @param id 文章ID
     * @param authorizationHeader 授权请求头
     * @return ApiResponse 响应对象
     */
    @Operation(summary = "获取单个文章", description = "根据文章ID获取单个文章详情")
    @ApiResponses({
         @ApiResponse(responseCode = "200", description = "获取成功，返回文章详情"),
         @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
         @ApiResponse(responseCode = "404", description = "获取失败，文章不存在"),
         @ApiResponse(responseCode = "500", description = "获取失败，服务器内部错误")
    })
    @GetMapping("/{id}")
    public ApiResult<Article> getArticle(@Parameter(description = "文章ID", required = true) @PathVariable Long id, @Parameter(description = "授权请求头，格式为Bearer token", required = true) @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromToken(authorizationHeader);
            Article article = articleService.getUserArticle(userId, id)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));
            return ApiResult.success(article);
        } catch (IllegalArgumentException e) {
            return ApiResult.fail(404, e.getMessage());
        } catch (Exception e) {
            log.error("Error getting article: {}", e.getMessage());
            return ApiResult.fail("Failed to get article");
        }
    }

    /**
     * 创建文章
     * @param article 文章实体
     * @param authorizationHeader 授权请求头
     * @return ApiResponse 响应对象
     */
    @Operation(summary = "创建文章", description = "创建新的文章")
    @ApiResponses({
         @ApiResponse(responseCode = "200", description = "创建成功，返回文章详情"),
         @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
         @ApiResponse(responseCode = "500", description = "创建失败，服务器内部错误")
    })
    @PostMapping
    public ApiResult<Article> createArticle(@Parameter(description = "文章实体", required = true) @RequestBody Article article, @Parameter(description = "授权请求头，格式为Bearer token", required = true) @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromToken(authorizationHeader);
            article.setUserId(userId);
            articleService.createArticle(article);
            return ApiResult.success(article);
        } catch (Exception e) {
            log.error("Error creating article: {}", e.getMessage());
            return ApiResult.fail("Failed to create article");
        }
    }

    /**
     * 更新文章
     * @param id 文章ID
     * @param article 文章实体
     * @param authorizationHeader 授权请求头
     * @return ApiResponse 响应对象
     */
    @Operation(summary = "更新文章", description = "根据文章ID更新文章")
    @ApiResponses({
         @ApiResponse(responseCode = "200", description = "更新成功，返回文章详情"),
         @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
         @ApiResponse(responseCode = "404", description = "更新失败，文章不存在"),
         @ApiResponse(responseCode = "500", description = "更新失败，服务器内部错误")
    })
    @PutMapping("/update/{id}")
    public ApiResult<Article> updateArticle(@Parameter(description = "文章ID", required = true) @PathVariable Long id, @Parameter(description = "文章实体", required = true) @RequestBody Article article, @Parameter(description = "授权请求头，格式为Bearer token", required = true) @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromToken(authorizationHeader);
            // 验证文章是否存在且属于当前用户
            Article existingArticle = articleService.getUserArticle(userId, id)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));
            
            // 更新文章
            existingArticle.setTitle(article.getTitle());
            existingArticle.setContent(article.getContent());
            articleService.updateArticle(existingArticle);
            
            return ApiResult.success(existingArticle);
        } catch (IllegalArgumentException e) {
            return ApiResult.fail(404, e.getMessage());
        } catch (Exception e) {
            log.error("Error updating article: {}", e.getMessage());
            return ApiResult.fail("Failed to update article");
        }
    }

    /**
     * 删除文章
     * @param id 文章ID
     * @param authorizationHeader 授权请求头
     * @return ApiResponse 响应对象
     */
    @Operation(summary = "删除文章", description = "根据文章ID删除文章")
    @ApiResponses({
         @ApiResponse(responseCode = "200", description = "删除成功，返回删除结果"),
         @ApiResponse(responseCode = "401", description = "未授权，token无效或过期"),
         @ApiResponse(responseCode = "404", description = "删除失败，文章不存在"),
         @ApiResponse(responseCode = "500", description = "删除失败，服务器内部错误")
    })
    @DeleteMapping("/delete/{id}")
    public ApiResult<String> deleteArticle(@Parameter(description = "文章ID", required = true) @PathVariable Long id, @Parameter(description = "授权请求头，格式为Bearer token", required = true) @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromToken(authorizationHeader);
            // 验证文章是否存在且属于当前用户
            Article existingArticle = articleService.getUserArticle(userId, id)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));
            
            // 删除文章
            articleService.deleteArticle(id);
            
            return ApiResult.success("Article deleted successfully");
        } catch (IllegalArgumentException e) {
            return ApiResult.fail(404, e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting article: {}", e.getMessage());
            return ApiResult.fail("Failed to delete article");
        }
    }

}
