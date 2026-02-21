package com.phonemall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.phonemall.common.BusinessException;
import com.phonemall.dto.CartDTO;
import com.phonemall.entity.CartItem;
import com.phonemall.entity.Product;
import com.phonemall.mapper.CartItemMapper;
import com.phonemall.mapper.ProductMapper;
import com.phonemall.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    @Override
    public List<CartItem> listCart(Long userId) {
        List<CartItem> items = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .orderByDesc(CartItem::getCreateTime));
        items.forEach(item -> {
            Product product = productMapper.selectById(item.getProductId());
            item.setProduct(product);
        });
        return items;
    }

    @Override
    public void addCart(Long userId, CartDTO dto) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        CartItem existing = cartItemMapper.selectOne(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .eq(CartItem::getProductId, dto.getProductId()));
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + dto.getQuantity());
            cartItemMapper.updateById(existing);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(dto.getProductId());
            item.setQuantity(dto.getQuantity());
            item.setSelected(1);
            cartItemMapper.insert(item);
        }
        log.info("添加购物车: userId={}, productId={}", userId, dto.getProductId());
    }

    @Override
    public void updateCart(Long id, Integer quantity, Long userId) {
        CartItem item = cartItemMapper.selectById(id);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        item.setQuantity(quantity);
        cartItemMapper.updateById(item);
    }

    @Override
    public void deleteCart(Long id, Long userId) {
        CartItem item = cartItemMapper.selectById(id);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        cartItemMapper.deleteById(id);
        log.info("删除购物车项: id={}", id);
    }

    @Override
    public void selectAll(Long userId, Integer selected) {
        CartItem update = new CartItem();
        update.setSelected(selected);
        cartItemMapper.update(update,
                new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
    }
}
