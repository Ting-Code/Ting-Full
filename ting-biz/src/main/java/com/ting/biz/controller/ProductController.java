package com.ting.biz.controller;

import com.ting.biz.dto.ProductSaveRequest;
import com.ting.biz.entity.BizProduct;
import com.ting.biz.service.ProductService;
import com.ting.common.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /** 写操作：网关要求 ADMIN */
    @PostMapping("/products")
    public R<BizProduct> create(@Valid @RequestBody ProductSaveRequest request) {
        return R.ok(productService.create(request));
    }

    @PutMapping("/products/{id}")
    public R<BizProduct> update(@PathVariable Long id, @Valid @RequestBody ProductSaveRequest request) {
        return R.ok(productService.update(id, request));
    }

    @DeleteMapping("/products/{id}")
    public R<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return R.ok();
    }
}
