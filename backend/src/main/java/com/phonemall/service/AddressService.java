package com.phonemall.service;

import com.phonemall.entity.Address;
import java.util.List;

public interface AddressService {
    List<Address> listAddress(Long userId);
    void saveAddress(Long userId, Address address);
    void deleteAddress(Long id, Long userId);
}
