package com.example.android.controller;

import com.example.android.common.Result;
import com.example.android.entity.Conversation;
import com.example.android.service.ConversationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    @Resource
    private ConversationService conversationService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getConversations(@RequestParam Long userId) {
        try {
            List<Map<String, Object>> conversations = conversationService.getConversationsWithLatestMessage(userId);
            return Result.success("获取成功", conversations);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取会话列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail/{conversationId}")
    public Result<Map<String, Object>> getConversationDetail(@PathVariable Long conversationId) {
        try {
            Map<String, Object> conversation = conversationService.getConversationDetail(conversationId);
            if (conversation != null) {
                return Result.success("获取成功", conversation);
            } else {
                return Result.notFound("会话不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取会话详情失败: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public Result<Long> createConversation(@RequestBody Map<String, Object> request) {
        try {
            Long initiatorId = ((Number) request.get("initiatorId")).longValue();
            Long receiverId = ((Number) request.get("receiverId")).longValue();
            Long productId = request.get("productId") != null ? ((Number) request.get("productId")).longValue() : null;
            String type = (String) request.get("type");

            Long conversationId = conversationService.createConversation(initiatorId, receiverId, productId, type);
            return Result.success("创建成功", conversationId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建会话失败: " + e.getMessage());
        }
    }

    @PutMapping("/close/{conversationId}")
    public Result<String> closeConversation(@PathVariable Long conversationId) {
        try {
            boolean success = conversationService.closeConversation(conversationId);
            return success ? Result.success("关闭成功") : Result.error("关闭失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("关闭会话失败: " + e.getMessage());
        }
    }

    @GetMapping("/find")
    public Result<Conversation> findConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2
    ) {
        try {
            Conversation conversation = conversationService.findConversation(userId1, userId2);
            return Result.success("获取成功", conversation);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查找会话失败: " + e.getMessage());
        }
    }
}
