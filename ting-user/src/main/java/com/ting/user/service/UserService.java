package com.ting.user.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ting.common.constant.AuthConstants;
import com.ting.common.exception.BizException;
import com.ting.user.dto.LoginRequest;
import com.ting.user.dto.LoginResponse;
import com.ting.user.entity.SysUser;
import com.ting.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper sysUserMapper;
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

        String token = IdUtil.fastSimpleUUID();
        stringRedisTemplate.opsForValue().set(
                AuthConstants.TOKEN_REDIS_PREFIX + token,
                String.valueOf(user.getId()),
                Duration.ofHours(24));

        return new LoginResponse(token, user.getId(), user.getUsername(), user.getNickname());
    }

    public SysUser getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user;
    }

    public Long resolveUserIdByToken(String token) {
        String userId = stringRedisTemplate.opsForValue().get(AuthConstants.TOKEN_REDIS_PREFIX + token);
        if (userId == null) {
            throw new BizException(401, "登录已失效");
        }
        stringRedisTemplate.expire(AuthConstants.TOKEN_REDIS_PREFIX + token, 24, TimeUnit.HOURS);
        return Long.valueOf(userId);
    }
}
