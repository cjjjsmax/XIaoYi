package com.example.android.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.android.entity.AiPrompt;
import com.example.android.mapper.AiPromptMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiPromptService extends ServiceImpl<AiPromptMapper, AiPrompt> {
    public String getProductInspectionPrompt(String category) {
        AiPrompt prompt = baseMapper.findByBizType(category);

        if (prompt == null) {
            throw new RuntimeException("未找到商品质检提示词模板，请确保ai_prompt表中存在对应分类(" + category + ")的提示词记录");
        }

        return prompt.getPromptTemplate();
    }

    public String getBizTypeByCategoryId(Integer categoryId) {
        if (categoryId == null) return "other";
        return switch (categoryId) {
            case 1 -> "digital";
            case 2 -> "fashion";
            case 3 -> "books";
            case 4 -> "sports";
            case 5 -> "other";
            default -> "other";
        };
    }
}
