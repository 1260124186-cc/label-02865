package com.phonemall.service;

import com.phonemall.dto.ReviewDTO;
import com.phonemall.dto.ReviewReplyDTO;
import com.phonemall.entity.ProductRating;
import com.phonemall.entity.Review;
import com.phonemall.mapper.ReviewMapper;

import java.util.List;

/**
 * 评价服务接口
 */
public interface ReviewService {

    /**
     * 创建评价
     */
    Review createReview(ReviewDTO reviewDTO, Long userId);

    /**
     * 查询商品的评价列表
     */
    List<ReviewMapper.ReviewWithUser> getProductReviews(Long productId, int page, int pageSize);

    /**
     * 查询商品的评价数量
     */
    int getReviewCount(Long productId);

    /**
     * 查询商品的评分统计
     */
    ProductRating getProductRating(Long productId);

    /**
     * 商家回复评价
     */
    void replyReview(ReviewReplyDTO replyDTO, Long adminId);
}
