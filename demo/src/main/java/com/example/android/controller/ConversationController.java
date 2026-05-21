package com.example.android.controller;

import com.example.android.entity.Conversation;
import com.example.android.service.ConversationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    @Resource
    private ConversationService conversationService;

    @GetMapping("/list")
    public Map<String, Object> getConversations(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> conversations = conversationService.getConversationsWithLatestMessage(userId);
            result.put("success", true);
            result.put("data", conversations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/detail/{conversationId}")
    public Map<String, Object> getConversationDetail(@PathVariable Long conversationId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String , Object> conversation = conversationService.getConversationDetail(conversationId);
            result.put("success", true);
            result.put("data", conversation);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> createConversation(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long initiatorId = ((Number) request.get("initiatorId")).longValue();
            Long receiverId = ((Number) request.get("receiverId")).longValue();
            Long productId = request.get("productId") != null ? ((Number) request.get("productId")).longValue() : null;
            String type = (String) request.get("type");

            Long conversationId = conversationService.createConversation(initiatorId, receiverId, productId, type);
            result.put("success", true);
            result.put("data", conversationId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/close/{conversationId}")
    public Map<String, Object> closeConversation(@PathVariable Long conversationId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = conversationService.closeConversation(conversationId);
            result.put("success", success);
            result.put("message", success ? "关闭成功" : "关闭失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/find")
    public Map<String, Object> findConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            Conversation conversation = conversationService.findConversation(userId1, userId2);
            result.put("success", true);
            result.put("data", conversation);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
