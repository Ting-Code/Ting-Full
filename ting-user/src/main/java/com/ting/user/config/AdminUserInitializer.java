package com.ting.user.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ting.user.entity.SysUser;
import com.ting.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 首次启动时确保有可用的 admin / 123456，方便你直接联调。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        SysUser admin = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, "admin"));
        if (admin == null) {
            SysUser user = new SysUser();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setNickname("管理员");
            user.setStatus(1);
            sysUserMapper.insert(user);
            log.info("已初始化默认账号 admin / 123456");
            return;
        }
        if (!passwordEncoder.matches("123456", admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode("123456"));
            sysUserMapper.updateById(admin);
            log.info("已重置 admin 密码为 123456");
        }
    }
}
