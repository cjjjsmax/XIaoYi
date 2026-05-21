package com.example.android.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_region")
public class Region {
    @TableId(value = "_id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    @TableField("region_id")
    private String code;
    
    @TableField("parent_id")
    private String parentId;
    
    private Integer level;
}
