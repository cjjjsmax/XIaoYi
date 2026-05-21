package com.example.android.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("ai_prompt")
public class AiPrompt {
    
    @TableId(type = IdType.AUTO)
    private Long id;

    private String bizType;

    private String promptTemplate;

    private String modelName;

    private Integer isActive;

    private Date createdAt;

    private Date updatedAt;
}