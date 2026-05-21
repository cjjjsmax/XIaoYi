package com.example.android.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.android.entity.AiPrompt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AiPromptMapper extends BaseMapper<AiPrompt> {

    @Select("SELECT * FROM ai_prompt WHERE `biz_type` = #{bizType} AND is_active = 1 LIMIT 1")
    AiPrompt findByBizType(String bizType);
}