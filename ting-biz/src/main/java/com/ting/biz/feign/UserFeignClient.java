package com.ting.biz.feign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ting.common.constant.ServiceNames;
import com.ting.common.result.R;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 服务间调用示例：biz → user
 * 先建立感性认识，细节（超时、降级）后面再学。
 */
@FeignClient(name = ServiceNames.USER)
public interface UserFeignClient {

    @GetMapping("/user/{id}")
    R<UserInfo> getUser(@PathVariable("id") Long id);

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    class UserInfo {
        private Long id;
        private String username;
        private String nickname;
    }
}
