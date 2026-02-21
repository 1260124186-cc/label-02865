package com.phonemall.controller;

import com.phonemall.common.Result;
import com.phonemall.dto.CartDTO;
import com.phonemall.entity.CartItem;
import com.phonemall.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/list")
    public Result<List<CartItem>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(cartService.listCart(userId));
    }

    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody CartDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.addCart(userId, dto);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestParam Long id, @RequestParam Integer quantity, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.updateCart(id, quantity, userId);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.deleteCart(id, userId);
        return Result.success();
    }

    @PutMapping("/select-all")
    public Result<?> selectAll(@RequestParam Integer selected, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.selectAll(userId, selected);
        return Result.success();
    }
}
