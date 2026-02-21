package com.phonemall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phonemall.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
