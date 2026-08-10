package com.ting.user.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ting.common.constant.AuthConstants;
import com.ting.user.entity.SysRole;
import com.ting.user.entity.SysUser;
import com.ting.user.mapper.SysRoleMapper;
import com.ting.user.mapper.SysUserMapper;
import com.ting.user.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 确保演示账号与角色就绪：admin=ADMIN，user=USER，密码均为 123456。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        ensureUser("admin", "管理员", AuthConstants.ROLE_ADMIN);
        ensureUser("user", "普通用户", AuthConstants.ROLE_USER);
    }

    private void ensureUser(String username, String nickname, String roleCode) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            user = new SysUser();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setNickname(nickname);
            user.setStatus(1);
            sysUserMapper.insert(user);
            log.info("已初始化账号 {} / 123456", username);
        } else if (!passwordEncoder.matches("123456", user.getPassword())) {
            user.setPassword(passwordEncoder.encode("123456"));
            sysUserMapper.updateById(user);
            log.info("已重置 {} 密码为 123456", username);
        }

        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getCode, roleCode));
        if (role == null) {
            log.warn("角色 {} 不存在，跳过绑定（请确认 Flyway V2 已执行）", roleCode);
            return;
        }
        if (sysUserRoleMapper.count(user.getId(), role.getId()) == 0) {
            sysUserRoleMapper.insert(user.getId(), role.getId());
            log.info("已绑定 {} -> {}", username, roleCode);
        }
    }
}
