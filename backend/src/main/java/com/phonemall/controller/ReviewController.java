package com.phonemall.controller;

import com.phonemall.common.Result;
import com.phonemall.dto.ReviewDTO;
import com.phonemall.dto.ReviewReplyDTO;
import com.phonemall.entity.ProductRating;
import com.phonemall.entity.Review;
import com.phonemall.mapper.ReviewMapper;
import com.phonemall.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 创建评价
     */
    @PostMapping
    public Result<Review> createReview(@Validated @RequestBody ReviewDTO reviewDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        Review review = reviewService.createReview(reviewDTO, userId);
        return Result.success(review);
    }

    /**
     * 查询商品的评价列表
     */
    @GetMapping("/product/{productId}")
    public Result<Map<String, Object>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        List<ReviewMapper.ReviewWithUser> reviews = reviewService.getProductReviews(productId, page, pageSize);
        int total = reviewService.getReviewCount(productId);
        ProductRating rating = reviewService.getProductRating(productId);

        Map<String, Object> result = new HashMap<>();
        result.put("reviews", reviews);
        result.put("total", total);
        result.put("rating", rating);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return Result.success(result);
    }

    /**
     * 商家回复评价（管理员权限）
     */
    @PostMapping("/reply")
    public Result<Void> replyReview(@Validated @RequestBody ReviewReplyDTO replyDTO, HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        if (adminId == null) {
            return Result.error(401, "请先登录");
        }
        // TODO: 检查是否为管理员权限
        reviewService.replyReview(replyDTO, adminId);
        return Result.success();
    }
}
