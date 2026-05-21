package com.example.android.controller;


import com.example.android.entity.UserAddress;
import com.example.android.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
public class UserAddressController {
    @Autowired
    private UserAddressService addressService;

    @GetMapping
    public Map<String, Object> getAddresses(@RequestParam(required = false) Long userId) {
        List<UserAddress> list;
        if (userId != null) {
            list = addressService.getAddressesByUserId(userId);
        } else {
            list = addressService.list();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getAddressById(@PathVariable Long id) {
        UserAddress address = addressService.getById(id);
        Map<String, Object> result = new HashMap<>();
        if (address != null) {
            result.put("success", true);
            result.put("data", address);
        } else {
            result.put("success", false);
            result.put("message", "地址不存在");
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> addAddress(@RequestBody UserAddress address) {
        boolean success = addressService.addAddress(address);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateAddress(@PathVariable Long id, @RequestBody UserAddress address) {
        address.setId(id);
        boolean success = addressService.updateAddress(address);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteAddress(@PathVariable Long id) {
        boolean success = addressService.removeById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
}
