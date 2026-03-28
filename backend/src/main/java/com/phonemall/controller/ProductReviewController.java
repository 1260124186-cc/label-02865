package com.phonemall.controller;

import com.phonemall.common.PageResult;
import com.phonemall.common.Result;
import com.phonemall.dto.ReviewCreateDTO;
import com.phonemall.dto.ReviewRatingDTO;
import com.phonemall.dto.ReviewReplyDTO;
import com.phonemall.entity.ProductReview;
import com.phonemall.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;

    @GetMapping("/product/{productId}")
    public Result<PageResult<ProductReview>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.getProductReviews(productId, page, size));
    }

    @GetMapping("/product/{productId}/rating")
    public Result<ReviewRatingDTO> getAverageRating(@PathVariable Long productId) {
        return Result.success(reviewService.getAverageRating(productId));
    }

    @PostMapping("/create")
    public Result<Void> createReview(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody ReviewCreateDTO dto) {
        reviewService.createReview(userId, dto);
        return Result.success();
    }

    @PostMapping("/reply")
    public Result<Void> replyToReview(@Valid @RequestBody ReviewReplyDTO dto) {
        reviewService.replyToReview(dto);
        return Result.success();
    }

    @GetMapping("/can-review")
    public Result<Boolean> canReview(
            @RequestAttribute("userId") Long userId,
            @RequestParam Long orderId,
            @RequestParam Long productId) {
        return Result.success(reviewService.canReview(userId, orderId, productId));
    }
}
