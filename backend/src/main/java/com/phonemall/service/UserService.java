package com.phonemall.service;

import com.phonemall.common.PageResult;
import com.phonemall.dto.LoginDTO;
import com.phonemall.dto.RegisterDTO;
import com.phonemall.entity.User;
import java.util.Map;

public interface UserService {
    void register(RegisterDTO dto);
    Map<String, Object> login(LoginDTO dto);
    User getUserInfo(Long userId);
    PageResult<User> listUsers(int page, int size, String keyword);
    void updateStatus(Long id, Integer status);
}
