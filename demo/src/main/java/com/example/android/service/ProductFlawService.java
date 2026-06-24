package com.example.android.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.android.entity.ProductFlaw;
import com.example.android.mapper.ProductFlawMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ProductFlawService extends ServiceImpl<ProductFlawMapper, ProductFlaw> {
    //查询商品瑕疵
    public List<ProductFlaw> getFlawsByProductId(Long productId) {
        return baseMapper.selectList(new LambdaQueryWrapper<ProductFlaw>()
                .eq(ProductFlaw::getProductId, productId));
    }

    //删除商品瑕疵记录
    @Transactional//在事务中执行
    public void deleteFlawsByProductId(Long productId) {
        baseMapper.delete(new LambdaQueryWrapper<ProductFlaw>()
                .eq(ProductFlaw::getProductId, productId));
    }

    //保存瑕疵记录
    @Transactional
    public void saveFlaws(Long productId, List<java.util.Map<String, String>> flaws) {
        if (flaws == null || flaws.isEmpty()) {
            return;
        }

        for (java.util.Map<String, String> flawMap : flaws) {
            ProductFlaw flaw = new ProductFlaw();
            flaw.setProductId(productId);
            flaw.setPart(flawMap.get("part"));
            flaw.setDesc(flawMap.get("desc"));
            baseMapper.insert(flaw);
        }
    }
}