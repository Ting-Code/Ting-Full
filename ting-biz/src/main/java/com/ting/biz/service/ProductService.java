package com.ting.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ting.biz.entity.BizProduct;
import com.ting.biz.feign.UserFeignClient;
import com.ting.biz.mapper.BizProductMapper;
import com.ting.common.exception.BizException;
import com.ting.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String CACHE_LIST_KEY = "biz:product:list";

    private final BizProductMapper productMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserFeignClient userFeignClient;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public List<BizProduct> list() {
        try {
            String cache = stringRedisTemplate.opsForValue().get(CACHE_LIST_KEY);
            if (StringUtils.hasText(cache)) {
                return objectMapper.readValue(
                        cache,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, BizProduct.class));
            }
        } catch (Exception ignored) {
            // 缓存异常不影响主流程
        }

        List<BizProduct> list = productMapper.selectList(new LambdaQueryWrapper<BizProduct>()
                .eq(BizProduct::getStatus, 1)
                .orderByDesc(BizProduct::getId));

        try {
            stringRedisTemplate.opsForValue().set(
                    CACHE_LIST_KEY,
                    objectMapper.writeValueAsString(list),
                    Duration.ofMinutes(5));
        } catch (Exception ignored) {
        }
        return list;
    }

    public BizProduct getById(Long id) {
        BizProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException("商品不存在");
        }
        return product;
    }

    public void clearListCache() {
        stringRedisTemplate.delete(CACHE_LIST_KEY);
    }

    /**
     * 演示 Feign：查商品时顺带拉一下创建人信息（示例用固定 userId=1）
     */
    public Map<String, Object> detailWithCreator(Long productId) {
        BizProduct product = getById(productId);
        Map<String, Object> result = new HashMap<>();
        result.put("product", product);

        R<UserFeignClient.UserInfo> userResp = userFeignClient.getUser(1L);
        if (userResp != null && userResp.getCode() == 0) {
            result.put("creator", userResp.getData());
        }
        return result;
    }
}
