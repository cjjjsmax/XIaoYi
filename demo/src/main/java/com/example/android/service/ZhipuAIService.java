package com.example.android.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZhipuAIService {
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Value("${zhipu.ai.api-key:6a97e9f0357741c4a76cf5c8e2576247.ynUECmumCZTmwK0d}")
    private String apiKey;

    @Value("${zhipu.ai.api-url:https://open.bigmodel.cn/api/paas/v4/chat/completions}")
    private String apiUrl;

    @Value("${zhipu.ai.model:GLM-5V-Turbo}")
    private String model;

    public ZhipuAIService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(java.time.Duration.ofSeconds(300))
                .readTimeout(java.time.Duration.ofSeconds(300))
                .writeTimeout(java.time.Duration.ofSeconds(300))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public String generateProductReport(String prompt, List<String> imageUrls) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 4096);

        List<Map<String, Object>> messageContents = new ArrayList<>();

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        messageContents.add(textContent);

        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                Map<String, Object> imageContent = new HashMap<>();
                imageContent.put("type", "image_url");
                Map<String, String> imageUrlObj = new HashMap<>();
                imageUrlObj.put("url", imageUrl);
                imageContent.put("image_url", imageUrlObj);
                messageContents.add(imageContent);
            }
        }
        List<Map<String, Object>> messages = List.of(Map.of(
                "role", "user",
                "content", messageContents
        ));
        requestBody.put("messages", messages);

        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(requestBody),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                System.out.println("AI请求失败，状态码: " + response.code());
                System.out.println("响应内容: " + responseBody);
                throw new IOException("AI请求失败，HTTP状态码: " + response.code() + ", 响应: " + responseBody);
            }
            return parseResponse(responseBody);
        }
    }

    public String generateProductReportWithBase64(String prompt, List<String> imageBase64List) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 4096);

        List<Map<String, Object>> messageContents = new ArrayList<>();

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        messageContents.add(textContent);

        if (imageBase64List != null && !imageBase64List.isEmpty()) {
            for (String base64Image : imageBase64List) {
                Map<String, Object> imageContent = new HashMap<>();
                imageContent.put("type", "image_url");
                Map<String, String> imageUrlObj = new HashMap<>();
                imageUrlObj.put("url", base64Image);
                imageContent.put("image_url", imageUrlObj);
                messageContents.add(imageContent);
            }
        }
        List<Map<String, Object>> messages = List.of(Map.of(
                "role", "user",
                "content", messageContents
        ));
        requestBody.put("messages", messages);

        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(requestBody),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                System.out.println("AI请求失败，状态码: " + response.code());
                System.out.println("响应内容: " + responseBody);
                throw new IOException("AI请求失败，HTTP状态码: " + response.code() + ", 响应: " + responseBody);
            }
            return parseResponse(responseBody);
        }
    }
    private String parseResponse(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);

        if (root.has("error")) {
            String errorMessage = root.get("error").get("message").asText();
            throw new RuntimeException("AI服务返回错误: " + errorMessage);
        }

        JsonNode choices = root.get("choices");
        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).get("message");
            if (message != null && message.has("content")) {
                return message.get("content").asText();
            }
        }

        throw new RuntimeException("无法从AI响应中提取报告内容");
    }

    public Map<String, Object> parseInspectionReport(String reportJson) throws IOException {
        Map<String, Object> result = new HashMap<>();

        try {
            String jsonContent = extractJsonFromResponse(reportJson);
            
            JsonNode root = objectMapper.readTree(jsonContent);

            if (root.has("overall_condition")) {
                result.put("overall_condition", root.get("overall_condition").asText());
            }

            if (root.has("flaws") && root.get("flaws").isArray()) {
                List<Map<String, String>> flaws = new ArrayList<>();
                JsonNode flawsArray = root.get("flaws");

                for (JsonNode flawNode : flawsArray) {
                    Map<String, String> flaw = new HashMap<>();
                    if (flawNode.has("part")) {
                        flaw.put("part", flawNode.get("part").asText());
                    }
                    if (flawNode.has("desc")) {
                        flaw.put("desc", flawNode.get("desc").asText());
                    }
                    flaws.add(flaw);
                }
                result.put("flaws", flaws);
            }

        } catch (Exception e) {
            result.put("raw_content", reportJson);
        }

        return result;
    }

    private String extractJsonFromResponse(String response) {
        int startIndex = response.indexOf("{");
        int endIndex = response.lastIndexOf("}");
        
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
        
        return response;
    }
}
