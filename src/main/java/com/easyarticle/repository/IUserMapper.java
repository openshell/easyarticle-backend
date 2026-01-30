package com.easyarticle.repository;

import com.easyarticle.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 用户数据访问接口
 * 提供用户相关的数据库操作方法
 */
@Mapper
public interface IUserMapper {

    /**
     * 插入用户
     * @param user 用户实体
     */
    void insert(User user);

    /**
     * 根据邮箱查找用户
     * @param email 用户邮箱
     * @return Optional<User> 用户 Optional 对象
     */
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return Optional<User> 用户 Optional 对象
     */
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     * @param email 用户邮箱
     * @return boolean 是否存在
     */
    boolean existsByEmail(@Param("email") String email);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return boolean 是否存在
     */
    boolean existsByUsername(@Param("username") String username);

}
