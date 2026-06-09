package com.example.android.controller;

import com.example.android.common.Result;
import com.example.android.entity.Order;
import com.example.android.entity.User;
import com.example.android.service.OrderService;
import com.example.android.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Resource
    private OrderService orderService;

    @Resource
    private UserService userService;

    private String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(date);
    }

    @GetMapping("/buyer")
    public Result<List<Map<String, Object>>> getOrdersByBuyer(@RequestParam("buyerId") Long buyerId) {
        try {
            List<Order> orders = orderService.getOrdersByBuyerId(buyerId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Order order : orders) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", order.getId());
                item.put("productId", order.getProductId());
                item.put("productTitle", order.getProductTitle());
                item.put("productImage", order.getProductImage());
                item.put("price", order.getPrice());
                item.put("sellerId", order.getSellerId());

                User seller = userService.getUserById(order.getSellerId());
                item.put("sellerName", seller != null ? seller.getUsername() : "未知用户");

                item.put("buyerId", order.getBuyerId());
                item.put("status", order.getStatus());
                item.put("createdAt", formatDate(order.getCreatedAt()));
                result.add(item);
            }
            return Result.success("获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取买家订单列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/seller")
    public Result<List<Map<String, Object>>> getOrdersBySeller(@RequestParam("sellerId") Long sellerId) {
        try {
            List<Order> orders = orderService.getOrdersBySellerId(sellerId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Order order : orders) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", order.getId());
                item.put("productId", order.getProductId());
                item.put("productTitle", order.getProductTitle());
                item.put("productImage", order.getProductImage());
                item.put("price", order.getPrice());
                item.put("sellerId", order.getSellerId());
                item.put("buyerId", order.getBuyerId());

                User buyer = userService.getUserById(order.getBuyerId());
                item.put("buyerName", buyer != null ? buyer.getUsername() : "未知用户");

                item.put("status", order.getStatus());
                item.put("createdAt", formatDate(order.getCreatedAt()));
                result.add(item);
            }
            return Result.success("获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取卖家订单列表失败: " + e.getMessage());
        }
    }
}