package com.example.android.service;

import com.example.android.entity.Conversation;
import com.example.android.entity.Message;
import com.example.android.entity.User;
import com.example.android.mapper.ConversationMapper;
import com.example.android.mapper.MessageMapper;
import com.example.android.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConversationService {
    @Resource
    private ConversationMapper conversationMapper;

    @Resource
    private MessageMapper messageMapper;
    
    @Resource
    private UserMapper userMapper;

    public List<Map<String, Object>> getConversationsWithLatestMessage(Long userId){
        List<Conversation> conversations = conversationMapper.getConversationsByUserId(userId);
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        for(Conversation conversation : conversations){
            Map<String, Object> conversationMap = new HashMap<>();
            conversationMap.put("id", conversation.getId());
            conversationMap.put("initiatorId", conversation.getInitiatorId());
            conversationMap.put("receiverId", conversation.getReceiverId());
            conversationMap.put("productId", conversation.getProductId());
            conversationMap.put("status", conversation.getStatus());
            conversationMap.put("createdAt", conversation.getCreatedAt());
            
            // 获取最新消息
            Message latestMessage = messageMapper.getLatestMessageByConversationId(conversation.getId());
            if(latestMessage != null) {
                conversationMap.put("lastMessage", latestMessage.getContent());
                conversationMap.put("lastMessageTime", latestMessage.getCreatedAt());
            } else {
                conversationMap.put("lastMessage", "");
                conversationMap.put("lastMessageTime", "");
            }
            
            // 计算对方用户ID
            Long otherUserId = conversation.getInitiatorId().equals(userId) ? conversation.getReceiverId() : conversation.getInitiatorId();
            conversationMap.put("otherUserId", otherUserId);
            
            // 获取对方用户信息
            User otherUser = userMapper.selectById(otherUserId);
            if(otherUser != null) {
                conversationMap.put("otherUserName", otherUser.getUsername());
                conversationMap.put("otherUserAvatar", otherUser.getAvatarUrl() != null ? otherUser.getAvatarUrl() : "");
            } else {
                conversationMap.put("otherUserName", "未知用户");
                conversationMap.put("otherUserAvatar", "");
            }
            
            // 计算未读消息数
            int unreadCount = messageMapper.countUnreadMessages(conversation.getId(), userId);
            conversationMap.put("unreadCount", unreadCount);
            
            resultList.add(conversationMap);
        }
        return resultList;
    }

    public Conversation getConversationById(Long id) {
        return conversationMapper.selectById(id);
    }

    public boolean createConversation(Conversation conversation) {
        return conversationMapper.insert(conversation) > 0;
    }

    public boolean updateConversation(Conversation conversation) {
        return conversationMapper.updateById(conversation) > 0;
    }

    public Long createConversation(Long initiatorId, Long receiverId, Long productId, String type) {
        Conversation conversation = new Conversation();
        conversation.setInitiatorId(initiatorId);
        conversation.setReceiverId(receiverId);
        conversation.setProductId(productId);
        conversation.setType(type);
        conversation.setStatus("active");
        conversationMapper.insert(conversation);
        return conversation.getId();
    }

    public Map<String, Object> getConversationDetail(Long conversationId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            return null;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", conversation.getId());
        result.put("initiatorId", conversation.getInitiatorId());
        result.put("receiverId", conversation.getReceiverId());
        result.put("productId", conversation.getProductId());
        result.put("type", conversation.getType());
        result.put("status", conversation.getStatus());
        result.put("createdAt", conversation.getCreatedAt());
        
        // 获取最新消息
        Message latestMessage = messageMapper.getLatestMessageByConversationId(conversationId);
        if (latestMessage != null) {
            result.put("lastMessage", latestMessage.getContent());
            result.put("lastMessageTime", latestMessage.getCreatedAt());
        } else {
            result.put("lastMessage", "");
            result.put("lastMessageTime", "");
        }
        
        return result;
    }

    public boolean closeConversation(Long conversationId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            return false;
        }
        conversation.setStatus("closed");
        return conversationMapper.updateById(conversation) > 0;
    }

    public Conversation findConversation(Long userId1, Long userId2) {
        return conversationMapper.findByUsers(userId1, userId2);
    }
}
