package com.example.android.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.android.entity.Product;
import com.example.android.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {

    //发布商品
    public boolean publishProduct(Product product,Long sellerId){
        product.setSellerId(sellerId);
        product.setStatus(1);
        Date now = new Date();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        return save(product);
    }
}
