package com.example.android.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long productId;
    private String productTitle;
    private String productImage;
    private Double price;
    private Long sellerId;
    private Long buyerId;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}
