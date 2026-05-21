package com.example.android.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("product_flaw")
public class ProductFlaw {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String part;
    @TableField("`desc`")
    private String desc;
    @TableField(exist = false)
    private LocalDateTime createdAt;
}