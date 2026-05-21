package com.example.android.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.android.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Select("SELECT * FROM orders WHERE buyer_id = #{buyerId} ORDER BY created_at DESC")
    @Results({
        @org.apache.ibatis.annotations.Result(property = "productId", column = "product_id"),
        @org.apache.ibatis.annotations.Result(property = "productTitle", column = "product_title"),
        @org.apache.ibatis.annotations.Result(property = "productImage", column = "product_image"),
        @org.apache.ibatis.annotations.Result(property = "sellerId", column = "seller_id"),
        @org.apache.ibatis.annotations.Result(property = "buyerId", column = "buyer_id"),
        @org.apache.ibatis.annotations.Result(property = "createdAt", column = "created_at"),
        @org.apache.ibatis.annotations.Result(property = "updatedAt", column = "updated_at")
    })
    List<Order> getOrdersByBuyerId(Long buyerId);

    @Select("SELECT * FROM orders WHERE seller_id = #{sellerId} ORDER BY created_at DESC")
    @Results({
        @org.apache.ibatis.annotations.Result(property = "productId", column = "product_id"),
        @org.apache.ibatis.annotations.Result(property = "productTitle", column = "product_title"),
        @org.apache.ibatis.annotations.Result(property = "productImage", column = "product_image"),
        @org.apache.ibatis.annotations.Result(property = "sellerId", column = "seller_id"),
        @org.apache.ibatis.annotations.Result(property = "buyerId", column = "buyer_id"),
        @org.apache.ibatis.annotations.Result(property = "createdAt", column = "created_at"),
        @org.apache.ibatis.annotations.Result(property = "updatedAt", column = "updated_at")
    })
    List<Order> getOrdersBySellerId(Long sellerId);
}