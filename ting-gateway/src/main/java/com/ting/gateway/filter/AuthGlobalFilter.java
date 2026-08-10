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
import java.util.List;

/**
 * 网关统一鉴权：查 Redis 中的 Token，通过后写入 X-User-Id 转发给下游。
 */
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /** 无需登录即可访问 */
    private static final List<String> WHITE_LIST = List.of(
            "/api/user/login"
    );

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // CORS 预检直接放行
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        String path = request.getURI().getPath();
        if (isWhiteListed(path)) {
            return chain.filter(stripClientUserId(exchange));
        }

        String token = request.getHeaders().getFirst(AuthConstants.TOKEN_HEADER);
        if (!StringUtils.hasText(token)) {
            return unauthorized(exchange, "未登录");
        }

        String redisKey = AuthConstants.TOKEN_REDIS_PREFIX + token;
        return redisTemplate.opsForValue().get(redisKey)
                .flatMap(userId -> {
                    if (!StringUtils.hasText(userId)) {
                        return unauthorized(exchange, "登录已失效");
                    }
                    ServerHttpRequest mutated = request.mutate()
                            .headers(headers -> headers.remove(AuthConstants.USER_ID_HEADER))
                            .header(AuthConstants.USER_ID_HEADER, userId)
                            .build();
                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .switchIfEmpty(unauthorized(exchange, "登录已失效"));
    }

    private boolean isWhiteListed(String path) {
        for (String pattern : WHITE_LIST) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /** 白名单接口也去掉客户端自带的 X-User-Id，防止伪造 */
    private ServerWebExchange stripClientUserId(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> headers.remove(AuthConstants.USER_ID_HEADER))
                .build();
        return exchange.mutate().request(request).build();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(R.fail(401, message));
        } catch (JsonProcessingException e) {
            bytes = ("{\"code\":401,\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
