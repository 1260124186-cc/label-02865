package com.phonemall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phonemall.entity.ProductReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductReviewMapper extends BaseMapper<ProductReview> {
    
    @Select("SELECT r.*, u.nickname, u.avatar FROM product_review r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "WHERE r.product_id = #{productId} " +
            "ORDER BY r.create_time DESC " +
            "LIMIT #{offset}, #{size}")
    List<ProductReview> findByProductIdWithUser(@Param("productId") Long productId, 
                                                @Param("offset") Integer offset, 
                                                @Param("size") Integer size);
    
    @Select("SELECT COUNT(*) FROM product_review WHERE product_id = #{productId}")
    Integer countByProductId(@Param("productId") Long productId);
    
    @Select("SELECT AVG(rating) FROM product_review WHERE product_id = #{productId}")
    Double getAverageRating(@Param("productId") Long productId);
}
