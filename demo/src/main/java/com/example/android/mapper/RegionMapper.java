package com.example.android.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.android.entity.Region;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RegionMapper extends BaseMapper<Region> {
    List<Region> selectByParentId(Long parentId);
    
    List<Region> selectByLevel(Integer level);
}
