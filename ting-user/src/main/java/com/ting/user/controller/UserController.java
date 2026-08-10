package com.ting.user.controller;

import com.ting.common.constant.AuthConstants;
import com.ting.common.result.R;
import com.ting.user.dto.LoginRequest;
import com.ting.user.dto.LoginResponse;
import com.ting.user.entity.SysUser;
import com.ting.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(userService.login(request));
    }

    @GetMapping("/{id}")
    public R<SysUser> detail(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    /**
     * 优先使用网关写入的 X-User-Id；直连调试时仍可用 X-Token。
     */
    @GetMapping("/me")
    public R<SysUser> me(
            @RequestHeader(value = AuthConstants.USER_ID_HEADER, required = false) Long userId,
            @RequestHeader(value = AuthConstants.TOKEN_HEADER, required = false) String token) {
        if (userId != null) {
            return R.ok(userService.getById(userId));
        }
        if (!StringUtils.hasText(token)) {
            return R.fail(401, "未登录");
        }
        return R.ok(userService.getById(userService.resolveUserIdByToken(token)));
    }
}
