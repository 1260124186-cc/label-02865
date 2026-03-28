package com.phonemall.service;

import com.phonemall.common.PageResult;
import com.phonemall.dto.ReviewCreateDTO;
import com.phonemall.dto.ReviewRatingDTO;
import com.phonemall.dto.ReviewReplyDTO;
import com.phonemall.entity.ProductReview;

public interface ProductReviewService {
    PageResult<ProductReview> getProductReviews(Long productId, int page, int size);
    ReviewRatingDTO getAverageRating(Long productId);
    void createReview(Long userId, ReviewCreateDTO dto);
    void replyToReview(ReviewReplyDTO dto);
    boolean canReview(Long userId, Long orderId, Long productId);
}
