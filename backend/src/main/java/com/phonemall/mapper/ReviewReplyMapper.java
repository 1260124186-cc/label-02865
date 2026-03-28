package com.phonemall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phonemall.entity.ReviewReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商家回复Mapper
 */
@Mapper
public interface ReviewReplyMapper extends BaseMapper<ReviewReply> {

    /**
     * 根据评价ID查询回复
     */
    @Select("SELECT * FROM review_reply WHERE review_id = #{reviewId}")
    ReviewReply selectByReviewId(@Param("reviewId") Long reviewId);
}
