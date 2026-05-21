package com.example.android.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("conversations")
public class Conversation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long initiatorId;
    private Long receiverId;
    private Long productId;
    private Long requestId;
    private String type;
    private String status;
    private String lastMessage;
    private Date lastMessageTime;
    private Date createdAt;
    private Date updatedAt;
}
