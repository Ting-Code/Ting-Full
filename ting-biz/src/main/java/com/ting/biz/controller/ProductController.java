package com.ting.biz.controller;

import com.ting.biz.entity.BizProduct;
import com.ting.biz.service.ProductService;
import com.ting.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/biz")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public R<List<BizProduct>> list() {
        return R.ok(productService.list());
    }

    @GetMapping("/products/{id}")
    public R<BizProduct> detail(@PathVariable Long id) {
        return R.ok(productService.getById(id));
    }

    @GetMapping("/products/{id}/with-creator")
    public R<Map<String, Object>> detailWithCreator(@PathVariable Long id) {
        return R.ok(productService.detailWithCreator(id));
    }
}
