package com.phonemall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.phonemall.common.BusinessException;
import com.phonemall.common.PageResult;
import com.phonemall.dto.OrderCreateDTO;
import com.phonemall.entity.*;
import com.phonemall.mapper.*;
import com.phonemall.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderInfoMapper orderInfoMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public OrderInfo createOrder(Long userId, OrderCreateDTO dto) {
        // 获取购物车选中项
        List<CartItem> cartItems = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .eq(CartItem::getSelected, 1));
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车中没有选中的商品");
        }

        // 获取收货地址
        Address address = addressMapper.selectById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("收货地址不存在");
        }

        // 生成订单号
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%06d", ThreadLocalRandom.current().nextInt(999999));

        // 计算总金额并创建订单项
        BigDecimal totalAmount = BigDecimal.ZERO;
        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStatus(0);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());
        order.setCreateTime(LocalDateTime.now());

        // 先计算总金额
        for (CartItem cartItem : cartItems) {
            Product product = productMapper.selectById(cartItem.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException("商品[" + (product != null ? product.getName() : "") + "]已下架");
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException("商品[" + product.getName() + "]库存不足");
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotalAmount(totalAmount);
        orderInfoMapper.insert(order);

        // 创建订单项 + 扣库存
        for (CartItem cartItem : cartItems) {
            Product product = productMapper.selectById(cartItem.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItemMapper.insert(orderItem);

            // 扣库存 + 加销量
            product.setStock(product.getStock() - cartItem.getQuantity());
            product.setSales(product.getSales() + cartItem.getQuantity());
            productMapper.updateById(product);
        }

        // 清空购物车选中项
        cartItemMapper.delete(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .eq(CartItem::getSelected, 1));

        log.info("创建订单成功: orderNo={}, userId={}, amount={}", orderNo, userId, totalAmount);
        return order;
    }

    @Override
    public PageResult<OrderInfo> listOrders(Long userId, Integer status, int page, int size) {
        Page<OrderInfo> p = new Page<>(page, size);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, userId);
        if (status != null && status >= 0) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime);
        Page<OrderInfo> result = orderInfoMapper.selectPage(p, wrapper);
        result.getRecords().forEach(this::fillOrderItems);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public OrderInfo getOrderDetail(Long orderId, Long userId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        fillOrderItems(order);
        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只能取消待付款订单");
        }
        order.setStatus(4);
        orderInfoMapper.updateById(order);

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(Math.max(0, product.getSales() - item.getQuantity()));
                productMapper.updateById(product);
            }
        }
        log.info("取消订单: orderId={}", orderId);
    }

    @Override
    public void confirmOrder(Long orderId, Long userId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("只能确认待收货订单");
        }
        order.setStatus(3);
        order.setFinishTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);
        log.info("确认收货: orderId={}", orderId);
    }

    @Override
    @Transactional
    public void payOrder(Long orderId, Long userId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只能支付待付款订单");
        }
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);
        log.info("订单支付成功: orderId={}", orderId);
    }

    @Override
    public PageResult<OrderInfo> adminListOrders(int page, int size, String orderNo, Integer status) {
        Page<OrderInfo> p = new Page<>(page, size);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(OrderInfo::getOrderNo, orderNo);
        }
        if (status != null && status >= 0) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime);
        Page<OrderInfo> result = orderInfoMapper.selectPage(p, wrapper);
        result.getRecords().forEach(this::fillOrderItems);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void shipOrder(Long orderId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("只能发货待发货订单");
        }
        order.setStatus(2);
        order.setShipTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);
        log.info("订单发货: orderId={}", orderId);
    }

    private void fillOrderItems(OrderInfo order) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        order.setItems(items);
    }
}
