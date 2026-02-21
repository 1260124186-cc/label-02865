package com.phonemall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.phonemall.common.BusinessException;
import com.phonemall.common.PageResult;
import com.phonemall.entity.Product;
import com.phonemall.mapper.ProductMapper;
import com.phonemall.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public PageResult<Product> listProducts(int page, int size, Long categoryId, String keyword) {
        Page<Product> p = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getName, keyword);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        Page<Product> result = productMapper.selectPage(p, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Product getProductDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        return product;
    }

    @Override
    public List<Product> getHotProducts() {
        return productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
                        .orderByDesc(Product::getSales)
                        .last("LIMIT 8"));
    }

    @Override
    public List<Product> getRecommendProducts() {
        List<Product> all = productMapper.selectList(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1));
        Collections.shuffle(all);
        return all.size() > 8 ? all.subList(0, 8) : all;
    }

    @Override
    public PageResult<Product> adminListProducts(int page, int size, String keyword, Integer status) {
        Page<Product> p = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        Page<Product> result = productMapper.selectPage(p, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void saveProduct(Product product) {
        if (product.getId() != null) {
            productMapper.updateById(product);
            log.info("更新商品: id={}", product.getId());
        } else {
            productMapper.insert(product);
            log.info("新增商品: name={}", product.getName());
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(status);
        productMapper.updateById(product);
        log.info("更新商品状态: id={}, status={}", id, status);
    }

    @Override
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
        log.info("删除商品: id={}", id);
    }
}
