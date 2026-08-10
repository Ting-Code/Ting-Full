package com.ting.user.controller;

import com.ting.common.constant.AuthConstants;
import com.ting.common.result.R;
import com.ting.user.dto.LoginRequest;
import com.ting.user.dto.LoginResponse;
import com.ting.user.dto.UserProfile;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 健康检查：不查库；经网关时需在白名单，否则无 Token 会 401 */
    @GetMapping("/ping")
    public R<String> ping() {
        return R.ok("pong");
    }

    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(userService.login(request));
    }

    /**
     * 按昵称模糊查询。注意：必须写在 /{id} 前面，否则 "search" 会被当成 id。
     */
    @GetMapping("/search")
    public R<List<SysUser>> searchByNickname(@RequestParam String nickname) {
        return R.ok(userService.searchByNickname(nickname));
    }

    @GetMapping("/{id}")
    public R<SysUser> detail(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    /**
     * 优先使用网关写入的 X-User-Id；直连调试时仍可用 X-Token。
     */
    @GetMapping("/me")
    public R<UserProfile> me(
            @RequestHeader(value = AuthConstants.USER_ID_HEADER, required = false) Long userId,
            @RequestHeader(value = AuthConstants.TOKEN_HEADER, required = false) String token) {
        if (userId != null) {
            return R.ok(userService.profile(userId));
        }
        if (!StringUtils.hasText(token)) {
            return R.fail(401, "未登录");
        }
        return R.ok(userService.profile(userService.resolveUserIdByToken(token)));
    }
}
