package com.example.android.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.android.entity.Region;
import com.example.android.mapper.RegionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService extends ServiceImpl<RegionMapper, Region> {
    public List<Region> getRegionsByParentId(String parentId) {
        QueryWrapper<Region> wrapper = new QueryWrapper<>();
        // 处理 parentId 为 "0" 或空字符串的情况，查询 parent_id 为 NULL 的记录
        if ("0".equals(parentId) || parentId == null || parentId.isEmpty()) {
            wrapper.isNull("parent_id");
        } else {
            wrapper.eq("parent_id", parentId);
        }
        wrapper.orderByAsc("region_id");
        return list(wrapper);
    }
    
    public List<Region> getRegionsByLevel(Integer level) {
        QueryWrapper<Region> wrapper = new QueryWrapper<>();
        wrapper.eq("level", level).orderByAsc("region_id");
        return list(wrapper);
    }
    
    public Region getRegionById(Long id) {
        return getById(id);
    }
}
