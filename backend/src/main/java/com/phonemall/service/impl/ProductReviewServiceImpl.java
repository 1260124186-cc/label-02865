package com.phonemall.service.impl;

import com.phonemall.common.BusinessException;
import com.phonemall.common.PageResult;
import com.phonemall.dto.ReviewCreateDTO;
import com.phonemall.dto.ReviewRatingDTO;
import com.phonemall.dto.ReviewReplyDTO;
import com.phonemall.entity.OrderInfo;
import com.phonemall.entity.OrderItem;
import com.phonemall.entity.ProductReview;
import com.phonemall.mapper.OrderInfoMapper;
import com.phonemall.mapper.OrderItemMapper;
import com.phonemall.mapper.ProductReviewMapper;
import com.phonemall.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewMapper reviewMapper;
    private final OrderInfoMapper orderInfoMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public PageResult<ProductReview> getProductReviews(Long productId, int page, int size) {
        int offset = (page - 1) * size;
        List<ProductReview> records = reviewMapper.findByProductIdWithUser(productId, offset, size);
        Integer total = reviewMapper.countByProductId(productId);
        return new PageResult<>(records, total.longValue(), page, size);
    }

    @Override
    public ReviewRatingDTO getAverageRating(Long productId) {
        Double avg = reviewMapper.getAverageRating(productId);
        Integer total = reviewMapper.countByProductId(productId);
        return new ReviewRatingDTO(
            avg != null ? avg : 0.0,
            total != null ? total.longValue() : 0L
        );
    }

    @Override
    @Transactional
    public void createReview(Long userId, ReviewCreateDTO dto) {
        if (!canReview(userId, dto.getOrderId(), dto.getProductId())) {
            throw new BusinessException("您无法对该商品进行评价");
        }

        ProductReview review = new ProductReview();
        review.setProductId(dto.getProductId());
        review.setOrderId(dto.getOrderId());
        review.setUserId(userId);
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());

        reviewMapper.insert(review);
        log.info("用户{}对商品{}创建了评价", userId, dto.getProductId());
    }

    @Override
    @Transactional
    public void replyToReview(ReviewReplyDTO dto) {
        ProductReview review = reviewMapper.selectById(dto.getReviewId());
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        if (review.getMerchantReply() != null) {
            throw new BusinessException("该评价已回复");
        }

        review.setMerchantReply(dto.getMerchantReply());
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
        log.info("商家回复了评价: reviewId={}", dto.getReviewId());
    }

    @Override
    public boolean canReview(Long userId, Long orderId, Long productId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        if (order.getStatus() != 3) {
            return false;
        }

        List<OrderItem> orderItems = orderItemMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
                .eq(OrderItem::getProductId, productId)
        );
        if (orderItems.isEmpty()) {
            return false;
        }

        Long existingReview = reviewMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductReview>()
                .eq(ProductReview::getOrderId, orderId)
                .eq(ProductReview::getProductId, productId)
        );

        return existingReview == 0;
    }
}
