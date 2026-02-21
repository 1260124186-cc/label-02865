package com.phonemall.service;

import com.phonemall.common.PageResult;
import com.phonemall.entity.Product;
import java.util.List;

public interface ProductService {
    PageResult<Product> listProducts(int page, int size, Long categoryId, String keyword);
    Product getProductDetail(Long id);
    List<Product> getHotProducts();
    List<Product> getRecommendProducts();
    PageResult<Product> adminListProducts(int page, int size, String keyword, Integer status);
    void saveProduct(Product product);
    void updateStatus(Long id, Integer status);
    void deleteProduct(Long id);
}
