package com.phonemall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.phonemall.common.BusinessException;
import com.phonemall.entity.Address;
import com.phonemall.mapper.AddressMapper;
import com.phonemall.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public List<Address> listAddress(Long userId) {
        return addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreateTime));
    }

    @Override
    public void saveAddress(Long userId, Address address) {
        address.setUserId(userId);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            // 取消其他默认地址
            Address update = new Address();
            update.setIsDefault(0);
            addressMapper.update(update,
                    new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        }
        if (address.getId() != null) {
            Address existing = addressMapper.selectById(address.getId());
            if (existing == null || !existing.getUserId().equals(userId)) {
                throw new BusinessException("地址不存在");
            }
            addressMapper.updateById(address);
            log.info("更新地址: id={}", address.getId());
        } else {
            addressMapper.insert(address);
            log.info("新增地址: userId={}", userId);
        }
    }

    @Override
    public void deleteAddress(Long id, Long userId) {
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }
        addressMapper.deleteById(id);
        log.info("删除地址: id={}", id);
    }
}
