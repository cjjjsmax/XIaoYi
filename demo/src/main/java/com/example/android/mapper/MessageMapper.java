package com.example.android.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.android.entity.Message;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {
    @Select("SELECT * FROM messages WHERE conversation_id = #{conversationId} ORDER BY created_at ASC")
    List<Message> getMessagesByConversationId(Long conversationId);

    @Select("SELECT * FROM messages WHERE conversation_id = #{conversationId} ORDER BY created_at DESC LIMIT 1")
    Message getLatestMessageByConversationId(Long conversationId);

    @Select("SELECT COUNT(*) FROM messages WHERE conversation_id = #{conversationId} AND receiver_id = #{userId} AND is_read = 0")
    int countUnreadMessages(Long conversationId, Long userId);

    @Update("UPDATE messages SET is_read = 1 WHERE conversation_id = #{conversationId} AND receiver_id = #{userId} AND is_read = 0")
    int markMessagesAsRead(Long conversationId, Long userId);

    @Select("SELECT * FROM messages WHERE sender_id = #{userId} OR receiver_id = #{userId} ORDER BY created_at DESC")
    List<Message> getMessagesByUserId(Long userId);
}
