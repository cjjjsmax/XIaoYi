package com.example.android.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.android.entity.Order;
import com.example.android.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {
    public List<Order> getOrdersByBuyerId(Long buyerId) {
        return baseMapper.getOrdersByBuyerId(buyerId);
    }

    public List<Order> getOrdersBySellerId(Long sellerId) {
        return baseMapper.getOrdersBySellerId(sellerId);
    }
}