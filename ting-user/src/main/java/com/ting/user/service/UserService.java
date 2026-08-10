package com.ting.user.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ting.common.constant.AuthConstants;
import com.ting.common.exception.BizException;
import com.ting.user.dto.LoginRequest;
import com.ting.user.dto.LoginResponse;
import com.ting.user.dto.UserProfile;
import com.ting.user.entity.SysUser;
import com.ting.user.mapper.SysRoleMapper;
import com.ting.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;

    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername()));
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException("账号已禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }

        List<String> roles = listRoles(user.getId());
        String token = IdUtil.fastSimpleUUID();
        Duration ttl = Duration.ofHours(24);
        stringRedisTemplate.opsForValue().set(
                AuthConstants.TOKEN_REDIS_PREFIX + token,
                String.valueOf(user.getId()),
                ttl);
        stringRedisTemplate.opsForValue().set(
                AuthConstants.ROLE_REDIS_PREFIX + token,
                String.join(",", roles),
                ttl);

        return new LoginResponse(token, user.getId(), user.getUsername(), user.getNickname(), roles);
    }

    /** 昵称模糊查询，返回 List（≈ JS 的 User[]） */
    public List<SysUser> searchByNickname(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return Collections.emptyList();
        }
        return sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .like(SysUser::getNickname, nickname.trim()));
    }

    public SysUser getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user;
    }

    public UserProfile profile(Long userId) {
        SysUser user = getById(userId);
        UserProfile profile = new UserProfile();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setNickname(user.getNickname());
        profile.setStatus(user.getStatus());
        profile.setRoles(listRoles(userId));
        return profile;
    }

    public List<String> listRoles(Long userId) {
        List<String> roles = sysRoleMapper.listCodesByUserId(userId);
        return roles == null ? Collections.emptyList() : roles;
    }

    public Long resolveUserIdByToken(String token) {
        String userId = stringRedisTemplate.opsForValue().get(AuthConstants.TOKEN_REDIS_PREFIX + token);
        if (userId == null) {
            throw new BizException(401, "登录已失效");
        }
        stringRedisTemplate.expire(AuthConstants.TOKEN_REDIS_PREFIX + token, 24, TimeUnit.HOURS);
        stringRedisTemplate.expire(AuthConstants.ROLE_REDIS_PREFIX + token, 24, TimeUnit.HOURS);
        return Long.valueOf(userId);
    }
}
