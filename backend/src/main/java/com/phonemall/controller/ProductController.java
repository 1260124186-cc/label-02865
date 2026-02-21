package com.phonemall.controller;

import com.phonemall.common.PageResult;
import com.phonemall.common.Result;
import com.phonemall.entity.Product;
import com.phonemall.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public Result<PageResult<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return Result.success(productService.listProducts(page, size, categoryId, keyword));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @GetMapping("/hot")
    public Result<List<Product>> hot() {
        return Result.success(productService.getHotProducts());
    }

    @GetMapping("/recommend")
    public Result<List<Product>> recommend() {
        return Result.success(productService.getRecommendProducts());
    }
}
