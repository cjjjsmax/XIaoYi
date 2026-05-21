package com.example.android.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.android.entity.ProductFlaw;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductFlawMapper extends BaseMapper<ProductFlaw> {
    List<ProductFlaw> selectByProductId(@Param("productId") Long productId);
    void deleteByProductId(@Param("productId") Long productId);
}
