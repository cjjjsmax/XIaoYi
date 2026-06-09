package com.example.android.controller;


import com.example.android.common.Result;
import com.example.android.entity.UserAddress;
import com.example.android.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
public class UserAddressController {
    @Autowired
    private UserAddressService addressService;

    @GetMapping
    public Result<List<UserAddress>> getAddresses(@RequestParam(required = false) Long userId) {
        try {
            List<UserAddress> list;
            if (userId != null) {
                list = addressService.getAddressesByUserId(userId);
            } else {
                list = addressService.list();
            }
            return Result.success("获取成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取地址列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<UserAddress> getAddressById(@PathVariable Long id) {
        try {
            UserAddress address = addressService.getById(id);
            if (address != null) {
                return Result.success("获取成功", address);
            } else {
                return Result.notFound("地址不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取地址失败: " + e.getMessage());
        }
    }

    @PostMapping
    public Result<String> addAddress(@RequestBody UserAddress address) {
        try {
            boolean success = addressService.addAddress(address);
            return success ? Result.success("添加成功") : Result.error("添加失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加地址失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<String> updateAddress(@PathVariable Long id, @RequestBody UserAddress address) {
        try {
            address.setId(id);
            boolean success = addressService.updateAddress(address);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新地址失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteAddress(@PathVariable Long id) {
        try {
            boolean success = addressService.removeById(id);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除地址失败: " + e.getMessage());
        }
    }
}
