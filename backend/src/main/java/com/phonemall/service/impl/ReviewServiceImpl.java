package com.phonemall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.phonemall.dto.ReviewDTO;
import com.phonemall.dto.ReviewReplyDTO;
import com.phonemall.entity.ProductRating;
import com.phonemall.entity.Review;
import com.phonemall.entity.ReviewReply;
import com.phonemall.mapper.ProductRatingMapper;
import com.phonemall.mapper.ReviewMapper;
import com.phonemall.mapper.ReviewReplyMapper;
import com.phonemall.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评价服务实现类
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private ReviewReplyMapper reviewReplyMapper;

    @Autowired
    private ProductRatingMapper productRatingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Review createReview(ReviewDTO reviewDTO, Long userId) {
        // 1. 创建评价
        Review review = new Review();
        review.setProductId(reviewDTO.getProductId());
        review.setUserId(userId);
        review.setOrderId(reviewDTO.getOrderId());
        review.setRating(reviewDTO.getRating());
        review.setContent(reviewDTO.getContent());
        review.setImages(reviewDTO.getImages());
        review.setStatus(1); // 默认审核通过

        reviewMapper.insert(review);

        // 2. 更新商品评分统计
        ProductRating productRating = productRatingMapper.selectByProductId(reviewDTO.getProductId());
        if (productRating == null) {
            // 首次创建评分统计
            productRating = new ProductRating();
            productRating.setProductId(reviewDTO.getProductId());
            productRating.setTotalRating(reviewDTO.getRating());
            productRating.setReviewCount(1);
            productRating.setRating1Count(reviewDTO.getRating() == 1 ? 1 : 0);
            productRating.setRating2Count(reviewDTO.getRating() == 2 ? 1 : 0);
            productRating.setRating3Count(reviewDTO.getRating() == 3 ? 1 : 0);
            productRating.setRating4Count(reviewDTO.getRating() == 4 ? 1 : 0);
            productRating.setRating5Count(reviewDTO.getRating() == 5 ? 1 : 0);
            productRatingMapper.insert(productRating);
        } else {
            // 更新现有评分统计
            productRating.setTotalRating(productRating.getTotalRating() + reviewDTO.getRating());
            productRating.setReviewCount(productRating.getReviewCount() + 1);
            switch (reviewDTO.getRating()) {
                case 1:
                    productRating.setRating1Count(productRating.getRating1Count() + 1);
                    break;
                case 2:
                    productRating.setRating2Count(productRating.getRating2Count() + 1);
                    break;
                case 3:
                    productRating.setRating3Count(productRating.getRating3Count() + 1);
                    break;
                case 4:
                    productRating.setRating4Count(productRating.getRating4Count() + 1);
                    break;
                case 5:
                    productRating.setRating5Count(productRating.getRating5Count() + 1);
                    break;
            }
            productRatingMapper.updateById(productRating);
        }

        return review;
    }

    @Override
    public List<ReviewMapper.ReviewWithUser> getProductReviews(Long productId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return reviewMapper.selectReviewListWithUser(productId, offset, pageSize);
    }

    @Override
    public int getReviewCount(Long productId) {
        return reviewMapper.countByProductId(productId);
    }

    @Override
    public ProductRating getProductRating(Long productId) {
        return productRatingMapper.selectByProductId(productId);
    }

    @Override
    public void replyReview(ReviewReplyDTO replyDTO, Long adminId) {
        // 检查是否已有回复
        ReviewReply existingReply = reviewReplyMapper.selectByReviewId(replyDTO.getReviewId());
        if (existingReply != null) {
            throw new RuntimeException("该评价已有回复");
        }

        ReviewReply reply = new ReviewReply();
        reply.setReviewId(replyDTO.getReviewId());
        reply.setAdminId(adminId);
        reply.setContent(replyDTO.getContent());

        reviewReplyMapper.insert(reply);
    }
}
