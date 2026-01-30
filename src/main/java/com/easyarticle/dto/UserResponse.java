package com.easyarticle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户响应DTO
 */
@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
}
