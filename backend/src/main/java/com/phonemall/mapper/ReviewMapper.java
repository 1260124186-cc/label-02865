package com.phonemall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phonemall.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评价Mapper
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 查询商品的评价列表（包含用户信息）
     */
    @Select("SELECT r.*, u.nickname, u.avatar " +
            "FROM review r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "WHERE r.product_id = #{productId} AND r.status = 1 " +
            "ORDER BY r.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<ReviewWithUser> selectReviewListWithUser(@Param("productId") Long productId,
                                                  @Param("offset") int offset,
                                                  @Param("limit") int limit);

    /**
     * 查询商品的评价数量
     */
    @Select("SELECT COUNT(*) FROM review WHERE product_id = #{productId} AND status = 1")
    int countByProductId(@Param("productId") Long productId);

    /**
     * 包含用户信息的评价VO
     */
    interface ReviewWithUser {
        Long getId();
        Long getProductId();
        Long getUserId();
        Integer getRating();
        String getContent();
        String getImages();
        Integer getStatus();
        java.time.LocalDateTime getCreateTime();
        String getNickname();
        String getAvatar();
    }
}
