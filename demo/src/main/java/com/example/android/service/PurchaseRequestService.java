package com.example.android.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.android.entity.PurchaseRequest;
import com.example.android.mapper.PurchaseRequestMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PurchaseRequestService extends ServiceImpl<PurchaseRequestMapper, PurchaseRequest> {
    public boolean publishPurchaseRequest(PurchaseRequest purchaseRequest) {
        purchaseRequest.setStatus(1);
        purchaseRequest.setViewCount(0);
        purchaseRequest.setCreatedAt(new Date());
        purchaseRequest.setUpdatedAt(new Date());
        return save(purchaseRequest);
    }
}
