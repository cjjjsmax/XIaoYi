package com.example.android.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("messages")
public class Message {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long conversationId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private String type;
    private String extraData;
    private Integer isRead;
    private Date createdAt;
}
