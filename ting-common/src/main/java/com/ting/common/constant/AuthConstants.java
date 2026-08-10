package com.ting.common.constant;

/**
 * 登录态约定：网关与 user 服务共用。
 */
public final class AuthConstants {

    /** 客户端请求头里的 Token */
    public static final String TOKEN_HEADER = "X-Token";

    /** 网关校验通过后写入、转发给下游的用户 ID（禁止客户端伪造，网关会先剥离） */
    public static final String USER_ID_HEADER = "X-User-Id";

    /** Redis 中 token → userId 的 key 前缀 */
    public static final String TOKEN_REDIS_PREFIX = "login:token:";

    private AuthConstants() {
    }
}
