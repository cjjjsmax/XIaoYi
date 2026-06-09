package com.example.android.controller;

import com.example.android.common.Result;
import com.example.android.entity.Message;
import com.example.android.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Resource
    private MessageService messageService;

    @GetMapping("/list")
    public Result<List<Message>> getMessages(@RequestParam Long userId) {
        try {
            List<Message> messages = messageService.getMessagesByUserId(userId);
            return Result.success("获取成功", messages);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取消息列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/conversation/{conversationId}")
    public Result<List<Message>> getConversationMessages(@PathVariable Long conversationId) {
        try {
            List<Message> messages = messageService.getMessagesByConversationId(conversationId);
            return Result.success("获取成功", messages);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取会话消息失败: " + e.getMessage());
        }
    }

    @PostMapping("/send")
    public Result<String> sendMessage(@RequestBody Message message) {
        try {
            boolean success = messageService.sendMessage(message);
            return success ? Result.success("发送成功") : Result.error("发送失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发送消息失败: " + e.getMessage());
        }
    }

    @PutMapping("/read/conversation/{conversationId}")
    public Result<String> markConversationAsRead(@PathVariable Long conversationId, @RequestParam Long userId) {
        try {
            boolean success = messageService.markConversationAsRead(conversationId, userId);
            return success ? Result.success("标记成功") : Result.error("标记失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("标记消息已读失败: " + e.getMessage());
        }
    }
}
