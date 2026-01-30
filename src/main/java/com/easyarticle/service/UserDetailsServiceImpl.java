package com.easyarticle.service;

import com.easyarticle.entity.User;
import com.easyarticle.repository.IUserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 用户详情服务实现类
 * 实现Spring Security的UserDetailsService接口
 * 用于加载用户信息进行认证
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserMapper iUserMapper;

    /**
     * 构造方法
     * @param iUserMapper 用户Mapper
     */
    public UserDetailsServiceImpl(IUserMapper iUserMapper) {
        this.iUserMapper = iUserMapper;
    }

    /**
     * 根据用户名加载用户详情
     * 尝试通过邮箱和用户名两种方式查找用户
     * @param username 用户名或邮箱
     * @return UserDetails 用户详情
     * @throws UsernameNotFoundException 用户名未找到异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 尝试通过邮箱查找用户
        User user = iUserMapper.findByEmail(username)
                .orElseGet(() -> iUserMapper.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username)));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

}
