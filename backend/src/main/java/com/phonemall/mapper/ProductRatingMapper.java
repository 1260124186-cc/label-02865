package com.phonemall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phonemall.entity.ProductRating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 商品评分统计Mapper
 */
@Mapper
public interface ProductRatingMapper extends BaseMapper<ProductRating> {

    /**
     * 根据商品ID查询评分统计
     */
    @Select("SELECT * FROM product_rating WHERE product_id = #{productId}")
    ProductRating selectByProductId(@Param("productId") Long productId);

    /**
     * 更新评分统计
     */
    @Update("UPDATE product_rating SET " +
            "total_rating = total_rating + #{rating}, " +
            "review_count = review_count + 1, " +
            "rating#{rating}_count = rating#{rating}_count + 1 " +
            "WHERE product_id = #{productId}")
    int updateRating(@Param("productId") Long productId, @Param("rating") Integer rating);
}
