package com.example.android.service;

import com.example.android.entity.Conversation;
import com.example.android.entity.Message;
import com.example.android.entity.User;
import com.example.android.mapper.ConversationMapper;
import com.example.android.mapper.MessageMapper;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    @Resource
    private MessageMapper messageMapper;

    @Resource
    private ConversationMapper conversationMapper;

    @Resource
    private UserService userService;


    public boolean sendMessage(Message message) {
        boolean messageInserted = messageMapper.insert(message) > 0;
        if (messageInserted) {
            Conversation conversation = conversationMapper.selectById(message.getConversationId());
            if (conversation != null) {
                conversation.setLastMessage(message.getContent());
                conversation.setLastMessageTime(message.getCreatedAt());
                conversationMapper.updateById(conversation);
            }
        }
        return messageInserted;
    }

    public List<Message> getMessagesByConversationId(Long conversationId) {
        return messageMapper.getMessagesByConversationId(conversationId);
    }

    public List<Message> getMessagesByUserId(Long userId) {
        return messageMapper.getMessagesByUserId(userId);
    }

    public boolean markConversationAsRead(Long conversationId, Long userId) {
        return messageMapper.markMessagesAsRead(conversationId, userId) > 0;
    }
}
