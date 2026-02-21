package com.phonemall.service;

import com.phonemall.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> listCategories();
    List<Category> adminListCategories();
    void saveCategory(Category category);
    void deleteCategory(Long id);
}
