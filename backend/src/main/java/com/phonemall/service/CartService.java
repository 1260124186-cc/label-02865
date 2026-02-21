package com.phonemall.service;

import com.phonemall.dto.CartDTO;
import com.phonemall.entity.CartItem;
import java.util.List;

public interface CartService {
    List<CartItem> listCart(Long userId);
    void addCart(Long userId, CartDTO dto);
    void updateCart(Long id, Integer quantity, Long userId);
    void deleteCart(Long id, Long userId);
    void selectAll(Long userId, Integer selected);
}
