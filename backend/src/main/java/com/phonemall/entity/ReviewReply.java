package com.phonemall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家回复实体类
 */
@Data
@TableName("review_reply")
public class ReviewReply {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评价ID
     */
    private Long reviewId;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 回复内容
     */
    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
