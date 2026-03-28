package com.phonemall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRatingDTO {
    private Double averageRating;
    private Long totalCount;
}
