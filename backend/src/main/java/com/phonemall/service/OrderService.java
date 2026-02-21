package com.phonemall.service;

import com.phonemall.common.PageResult;
import com.phonemall.dto.OrderCreateDTO;
import com.phonemall.entity.OrderInfo;

public interface OrderService {
    OrderInfo createOrder(Long userId, OrderCreateDTO dto);
    PageResult<OrderInfo> listOrders(Long userId, Integer status, int page, int size);
    OrderInfo getOrderDetail(Long orderId, Long userId);
    void cancelOrder(Long orderId, Long userId);
    void confirmOrder(Long orderId, Long userId);
    void payOrder(Long orderId, Long userId);
    PageResult<OrderInfo> adminListOrders(int page, int size, String orderNo, Integer status);
    void shipOrder(Long orderId);
}
