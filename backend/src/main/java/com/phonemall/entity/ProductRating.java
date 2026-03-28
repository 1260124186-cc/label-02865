package com.phonemall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品评分统计实体类
 */
@Data
@TableName("product_rating")
public class ProductRating {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 总评分
     */
    private Integer totalRating;

    /**
     * 评价数量
     */
    private Integer reviewCount;

    /**
     * 1星评价数量
     */
    private Integer rating1Count;

    /**
     * 2星评价数量
     */
    private Integer rating2Count;

    /**
     * 3星评价数量
     */
    private Integer rating3Count;

    /**
     * 4星评价数量
     */
    private Integer rating4Count;

    /**
     * 5星评价数量
     */
    private Integer rating5Count;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
