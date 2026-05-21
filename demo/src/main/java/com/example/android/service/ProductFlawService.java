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
    public List<ProductFlaw> getFlawsByProductId(Long productId) {
        System.out.println("查询缺陷列表，productId=" + productId);
        List<ProductFlaw> flaws = baseMapper.selectList(new LambdaQueryWrapper<ProductFlaw>()
                .eq(ProductFlaw::getProductId, productId));
        System.out.println("查询结果：" + (flaws != null ? flaws.size() : 0) + "条记录");
        if (flaws != null && !flaws.isEmpty()) {
            for (ProductFlaw flaw : flaws) {
                System.out.println("缺陷记录：part=" + flaw.getPart() + ", desc=" + flaw.getDesc());
            }
        }
        return flaws;
    }

    @Transactional
    public void deleteFlawsByProductId(Long productId) {
        baseMapper.delete(new LambdaQueryWrapper<ProductFlaw>()
                .eq(ProductFlaw::getProductId, productId));
    }

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