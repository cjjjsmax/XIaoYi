package com.example.android.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("products")
public class Product {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long sellerId;
    private String title;
    private String description;
    private Integer categoryId;
    private Double price;
    private String images;
    private Integer status;
    private Integer viewCount;
    private String overallCondition;
    private Date createdAt;
    private Date updatedAt;
}
