package com.easyarticle.config;

import com.easyarticle.util.JwtUtil;
import com.easyarticle.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 用于验证JWT令牌并设置认证上下文
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 构造方法
     * @param jwtUtil JWT工具类
     * @param userDetailsService 用户详情服务
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 过滤器处理方法
     * @param request 请求对象
     * @param response 响应对象
     * @param filterChain 过滤器链
     * @throws ServletException 异常
     * @throws IOException 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        // 检查是否是认证相关的请求
        if (request.getServletPath().startsWith("/auth/")) {
            // 认证请求直接放行
            filterChain.doFilter(request, response);
            return;
        }

        // 检查Authorization头是否存在且格式正确
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // 缺少或格式错误的Authorization头，返回401错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized: Missing or invalid Authorization header\",\"data\":null}");
            return;
        }

        String token = authorizationHeader.substring(7);
        String username = null;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.ExpiredJwtException | io.jsonwebtoken.UnsupportedJwtException | IllegalArgumentException e) {
            // 解析token失败，返回401错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized: Invalid token\",\"data\":null}");
            return;
        }

        // 如果token有效且用户未认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // 验证token
                if (jwtUtil.validateToken(token, userDetails)) {
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 设置认证上下文
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    // token无效，返回401错误
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized: Invalid token\",\"data\":null}");
                    return;
                }
            } catch (Exception e) {
                // 用户不存在或token无效，返回401错误
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized: User not found\",\"data\":null}");
                return;
            }
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
