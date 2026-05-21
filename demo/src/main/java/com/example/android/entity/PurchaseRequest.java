package com.example.android.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("purchase_requests")
public class PurchaseRequest {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long buyerId;
    private String title;
    private String description;
    private Integer categoryId;
    private Double maxPrice;
    private Integer status;
    private Integer viewCount;
    private Date createdAt;
    private Date updatedAt;
}
