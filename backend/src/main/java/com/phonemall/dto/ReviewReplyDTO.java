package com.phonemall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商家回复DTO
 */
@Data
public class ReviewReplyDTO {

    /**
     * 评价ID
     */
    @NotNull(message = "评价ID不能为空")
    private Long reviewId;

    /**
     * 回复内容
     */
    @NotBlank(message = "回复内容不能为空")
    private String content;
}
