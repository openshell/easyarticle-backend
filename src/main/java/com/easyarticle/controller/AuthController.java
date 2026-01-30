package com.easyarticle.controller;

import com.easyarticle.dto.LoginRequest;
import com.easyarticle.dto.RegisterRequest;
import com.easyarticle.dto.UserResponse;
import com.easyarticle.entity.User;
import com.easyarticle.repository.IUserMapper;
import com.easyarticle.service.UserDetailsServiceImpl;
import com.easyarticle.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、注册、登出和获取用户信息等操作
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final IUserMapper iUserMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造方法
     * @param authenticationManager 认证管理器
     * @param userDetailsService 用户详情服务
     * @param jwtUtil JWT工具类
     * @param iUserMapper 用户Mapper
     * @param passwordEncoder 密码编码器
     */
    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, IUserMapper iUserMapper, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.iUserMapper = iUserMapper;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * 用户登录接口
     * @param loginRequest 登录请求
     * @return ResponseEntity 响应实体
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 验证用户凭证
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // 加载用户详情
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        // 生成JWT token
        final String token = jwtUtil.generateToken(userDetails);

        // 获取用户信息
        User user = iUserMapper.findByEmail(loginRequest.getEmail()).orElse(null);

        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", new UserResponse(user.getId(), user.getUsername(), user.getEmail()));

        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前用户信息接口
     * @param authorizationHeader 授权请求头
     * @return ResponseEntity 响应实体
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        // 从请求头中获取token
        String token = authorizationHeader.substring(7); // 移除"Bearer "前缀

        // 从token中获取用户名
        String email = jwtUtil.extractUsername(token);

        // 获取用户信息
        User user = iUserMapper.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 构建响应
        UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail());

        return ResponseEntity.ok(userResponse);
    }

    /**
     * 用户登出接口
     * 由于使用的是JWT，登出操作主要在前端进行，后端可以不做处理
     * 或者可以实现token黑名单机制
     * @return ResponseEntity 响应实体
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logout successful");
    }

    /**
     * 用户注册接口
     * @param registerRequest 注册请求
     * @return ResponseEntity 响应实体
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        // 检查邮箱是否已存在
        if (iUserMapper.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 检查用户名是否已存在
        if (iUserMapper.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // 保存用户
        iUserMapper.insert(user);

        // 构建响应
        UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

}
