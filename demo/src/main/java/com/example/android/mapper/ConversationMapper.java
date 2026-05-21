package com.example.android.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.android.entity.Conversation;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ConversationMapper extends BaseMapper<Conversation> {
    @Select("SELECT * FROM conversations WHERE initiator_id = #{userId} OR receiver_id = #{userId} ORDER BY updated_at DESC")
    List<Conversation> getConversationsByUserId(Long userId);
    @Select("SELECT * FROM conversations WHERE (user1_id = #{userId1} AND user2_id = #{userId2}) OR (user1_id = #{userId2} AND user2_id = #{userId1}) LIMIT 1")
    Conversation findByUsers(Long userId1, Long userId2);
}
