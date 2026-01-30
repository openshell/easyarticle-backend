package com.easyarticle.controller;

import com.easyarticle.constant.Constants;
import com.easyarticle.dto.ApiResult;
import com.easyarticle.dto.ArticleRequestDTO;
import com.easyarticle.dto.ArticleResponseDTO;
import com.easyarticle.entity.Article;
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

    /**
     * 从Authorization头中获取用户ID
     * @param authorizationHeader 授权请求头
     * @return Long 用户ID
     */
    private Long getUserIdFromToken(String authorizationHeader) {
        String token = authorizationHeader.substring(Constants.JWT.BEARER_PREFIX_LENGTH); // 移除"Bearer "前缀
        return jwtUtil.extractUserId(token);
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
    public ApiResult<List<ArticleResponseDTO>> getUserArticles(@Parameter(description = "授权请求头，格式为Bearer token", required = true) @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = getUserIdFromToken(authorizationHeader);
        List<Article> articles = articleService.getUserArticles(userId);
        List<ArticleResponseDTO> responseDTOS = articles.stream()
                .map(article -> new ArticleResponseDTO(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getCreatedAt(),
                        article.getUpdatedAt()))
                .collect(java.util.stream.Collectors.toList());
        return ApiResult.success(responseDTOS);
    }

    /**
     * 测试接口
     * @return ApiResponse 响应对象
     */
    @Operation(summary = "测试接口", description = "测试JWT认证过滤器是否正常工作")
    @GetMapping("/test")
    public ApiResult<String> test() {
        return ApiResult.success("Test successful");
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
    public ApiResult<ArticleResponseDTO> getArticle(@Parameter(description = "文章ID", required = true) @PathVariable Long id, @Parameter(description = "授权请求头，格式为Bearer token", required = true) @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = getUserIdFromToken(authorizationHeader);
        Article article = articleService.getUserArticle(userId, id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));
        ArticleResponseDTO responseDTO = new ArticleResponseDTO(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getCreatedAt(),
                article.getUpdatedAt());
        return ApiResult.success(responseDTO);
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
    public ApiResult<ArticleResponseDTO> createArticle(@Parameter(description = "文章请求参数", required = true) @RequestBody ArticleRequestDTO articleRequestDTO, @Parameter(description = "授权请求头，格式为Bearer token", required = true) @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = getUserIdFromToken(authorizationHeader);
        Article article = new Article();
        article.setUserId(userId);
        article.setTitle(articleRequestDTO.getTitle());
        article.setContent(articleRequestDTO.getContent());
        articleService.createArticle(article);
        ArticleResponseDTO responseDTO = new ArticleResponseDTO(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getCreatedAt(),
                article.getUpdatedAt());
        return ApiResult.success(responseDTO);
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
    public ApiResult<ArticleResponseDTO> updateArticle(@Parameter(description = "文章ID", required = true) @PathVariable Long id, @Parameter(description = "文章请求参数", required = true) @RequestBody ArticleRequestDTO articleRequestDTO, @Parameter(description = "授权请求头，格式为Bearer token", required = true) @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = getUserIdFromToken(authorizationHeader);
        // 验证文章是否存在且属于当前用户
        Article existingArticle = articleService.getUserArticle(userId, id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));
        
        // 更新文章
        existingArticle.setTitle(articleRequestDTO.getTitle());
        existingArticle.setContent(articleRequestDTO.getContent());
        articleService.updateArticle(existingArticle);
        
        ArticleResponseDTO responseDTO = new ArticleResponseDTO(
                existingArticle.getId(),
                existingArticle.getTitle(),
                existingArticle.getContent(),
                existingArticle.getCreatedAt(),
                existingArticle.getUpdatedAt());
        return ApiResult.success(responseDTO);
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
        Long userId = getUserIdFromToken(authorizationHeader);
        // 验证文章是否存在且属于当前用户
        articleService.getUserArticle(userId, id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));
        
        // 删除文章
        articleService.deleteArticle(id);
        
        return ApiResult.success("Article deleted successfully");
    }

}
