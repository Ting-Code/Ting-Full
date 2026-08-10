package com.ting.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ting.common.constant.AuthConstants;
import com.ting.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 网关统一鉴权：Token → 用户；写商品接口额外要求 ADMIN 角色。
 */
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final List<String> WHITE_LIST = List.of(
            "/api/user/login"
    );

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        String path = request.getURI().getPath();
        if (isWhiteListed(path)) {
            return chain.filter(stripClientAuthHeaders(exchange));
        }

        String token = request.getHeaders().getFirst(AuthConstants.TOKEN_HEADER);
        if (!StringUtils.hasText(token)) {
            return unauthorized(exchange, "未登录");
        }

        String userKey = AuthConstants.TOKEN_REDIS_PREFIX + token;
        String roleKey = AuthConstants.ROLE_REDIS_PREFIX + token;

        return redisTemplate.opsForValue().get(userKey)
                .flatMap(userId -> {
                    if (!StringUtils.hasText(userId)) {
                        return unauthorized(exchange, "登录已失效");
                    }
                    return redisTemplate.opsForValue().get(roleKey)
                            .defaultIfEmpty("")
                            .flatMap(rolesCsv -> {
                                Set<String> roles = parseRoles(rolesCsv);
                                if (requiresAdmin(request.getMethod(), path)
                                        && !roles.contains(AuthConstants.ROLE_ADMIN)) {
                                    return forbidden(exchange, "需要管理员权限");
                                }
                                ServerHttpRequest mutated = request.mutate()
                                        .headers(headers -> {
                                            headers.remove(AuthConstants.USER_ID_HEADER);
                                            headers.remove(AuthConstants.ROLES_HEADER);
                                        })
                                        .header(AuthConstants.USER_ID_HEADER, userId)
                                        .header(AuthConstants.ROLES_HEADER, String.join(",", roles))
                                        .build();
                                return chain.filter(exchange.mutate().request(mutated).build());
                            });
                })
                .switchIfEmpty(unauthorized(exchange, "登录已失效"));
    }

    private boolean requiresAdmin(HttpMethod method, String path) {
        boolean productPath = PATH_MATCHER.match("/api/biz/products", path)
                || PATH_MATCHER.match("/api/biz/products/**", path);
        if (!productPath) {
            return false;
        }
        return HttpMethod.POST.equals(method)
                || HttpMethod.PUT.equals(method)
                || HttpMethod.DELETE.equals(method)
                || HttpMethod.PATCH.equals(method);
    }

    private Set<String> parseRoles(String rolesCsv) {
        if (!StringUtils.hasText(rolesCsv)) {
            return Set.of();
        }
        return Arrays.stream(rolesCsv.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    private boolean isWhiteListed(String path) {
        for (String pattern : WHITE_LIST) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private ServerWebExchange stripClientAuthHeaders(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(AuthConstants.USER_ID_HEADER);
                    headers.remove(AuthConstants.ROLES_HEADER);
                })
                .build();
        return exchange.mutate().request(request).build();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        return writeJson(exchange, HttpStatus.UNAUTHORIZED, R.fail(401, message));
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        return writeJson(exchange, HttpStatus.FORBIDDEN, R.fail(403, message));
    }

    private Mono<Void> writeJson(ServerWebExchange exchange, HttpStatus status, R<?> body) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = ("{\"code\":" + status.value() + ",\"message\":\"" + body.getMessage() + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
