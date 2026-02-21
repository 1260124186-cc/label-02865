package com.phonemall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("banner")
public class Banner {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String imageUrl;
    private Long productId;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
}
