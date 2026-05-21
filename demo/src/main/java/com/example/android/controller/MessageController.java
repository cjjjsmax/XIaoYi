package com.example.android.controller;

import com.example.android.entity.Message;
import com.example.android.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Resource
    private MessageService messageService;

    @GetMapping("/list")
    public Map<String, Object> getMessages(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Message> messages = messageService.getMessagesByUserId(userId);
            result.put("success", true);
            result.put("data", messages);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/conversation/{conversationId}")
    public Map<String, Object> getConversationMessages(@PathVariable Long conversationId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Message> messages = messageService.getMessagesByConversationId(conversationId);
            result.put("success", true);
            result.put("data", messages);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody Message message) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageService.sendMessage(message);
            result.put("success", success);
            result.put("message", success ? "发送成功" : "发送失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/read/conversation/{conversationId}")
    public Map<String, Object> markConversationAsRead(@PathVariable Long conversationId, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageService.markConversationAsRead(conversationId, userId);
            result.put("success", success);
            result.put("message", success ? "标记成功" : "标记失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
