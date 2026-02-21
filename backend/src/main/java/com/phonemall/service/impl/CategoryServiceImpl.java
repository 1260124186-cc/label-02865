package com.phonemall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.phonemall.entity.Category;
import com.phonemall.mapper.CategoryMapper;
import com.phonemall.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> listCategories() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSortOrder));
    }

    @Override
    public List<Category> adminListCategories() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
    }

    @Override
    public void saveCategory(Category category) {
        if (category.getId() != null) {
            categoryMapper.updateById(category);
            log.info("更新分类: id={}", category.getId());
        } else {
            categoryMapper.insert(category);
            log.info("新增分类: name={}", category.getName());
        }
    }

    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
        log.info("删除分类: id={}", id);
    }
}
