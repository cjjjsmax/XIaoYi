package com.example.android.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.android.common.Result;
import com.example.android.entity.PurchaseRequest;
import com.example.android.entity.User;
import com.example.android.service.PurchaseRequestService;
import com.example.android.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/purchase-requests")
public class PurchaseRequestController {
    @Autowired
    private PurchaseRequestService purchaseRequestService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getPurchaseRequestList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId) {
        try {
            QueryWrapper<PurchaseRequest> queryWrapper = new QueryWrapper<PurchaseRequest>()
                    .orderByDesc("created_at");
            if (keyword != null && !keyword.isEmpty()) {
                queryWrapper.like("title", keyword);
            }
            if (categoryId != null && categoryId > 0) {
                queryWrapper.eq("category_id", categoryId);
            }
            List<PurchaseRequest> requests = purchaseRequestService.list(queryWrapper);
            List<Map<String, Object>> result = new ArrayList<>();
            for (PurchaseRequest request : requests) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", request.getId());
                item.put("title", request.getTitle());
                item.put("maxPrice", request.getMaxPrice());
                item.put("description", request.getDescription());
                item.put("categoryId", request.getCategoryId());
                item.put("buyerId", request.getBuyerId());
                User buyer = userService.getUserById(request.getBuyerId());
                item.put("buyerName", buyer != null ? buyer.getUsername() : "未知用户");
                item.put("status", request.getStatus());
                item.put("viewCount", request.getViewCount());
                item.put("createdAt", request.getCreatedAt());
                result.add(item);
            }
            return Result.success("获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取求购列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/publish")
    public Result<String> publishPurchaseRequest(@RequestBody Map<String, Object> request) {
        try {
            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setTitle((String) request.get("title"));
            purchaseRequest.setMaxPrice(((Number) request.get("maxPrice")).doubleValue());
            purchaseRequest.setDescription((String) request.get("description"));
            purchaseRequest.setCategoryId((Integer) request.get("categoryId"));
            purchaseRequest.setBuyerId(((Number) request.get("buyerId")).longValue());
            purchaseRequest.setStatus(1);
            purchaseRequest.setViewCount(0);
            purchaseRequest.setCreatedAt(new Date());
            purchaseRequest.setUpdatedAt(new Date());

            boolean result = purchaseRequestService.save(purchaseRequest);
            return result ? Result.success("发布成功") : Result.error("发布失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发布失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail")
    public Result<Map<String, Object>> getPurchaseRequestById(@RequestParam("id") Long id) {
        try {
            PurchaseRequest request = purchaseRequestService.getById(id);
            if (request != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", request.getId());
                data.put("title", request.getTitle());
                data.put("maxPrice", request.getMaxPrice());
                data.put("description", request.getDescription());
                data.put("categoryId", request.getCategoryId());
                data.put("buyerId", request.getBuyerId());
                data.put("status", request.getStatus());
                data.put("viewCount", request.getViewCount());
                data.put("createdAt", request.getCreatedAt());
                return Result.success("获取成功", data);
            } else {
                return Result.notFound("求购信息不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<String> updatePurchaseRequest(
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("maxPrice") Double maxPrice,
            @RequestParam("description") String description) {
        try {
            PurchaseRequest purchaseRequest = purchaseRequestService.getById(id);
            if (purchaseRequest != null) {
                purchaseRequest.setTitle(title);
                purchaseRequest.setMaxPrice(maxPrice);
                purchaseRequest.setDescription(description);
                purchaseRequest.setUpdatedAt(new Date());
                boolean result = purchaseRequestService.updateById(purchaseRequest);
                return result ? Result.success("更新成功") : Result.error("更新失败");
            } else {
                return Result.notFound("求购信息不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public Result<String> deletePurchaseRequest(@RequestParam("id") Long id) {
        try {
            boolean result = purchaseRequestService.removeById(id);
            return result ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public Result<List<Map<String, Object>>> getPurchaseRequestsByUser(@RequestParam("buyerId") Long buyerId) {
        try {
            List<PurchaseRequest> requests = purchaseRequestService.list(
                    new QueryWrapper<PurchaseRequest>()
                            .eq("buyer_id", buyerId)
                            .orderByDesc("created_at")
            );
            List<Map<String, Object>> result = new ArrayList<>();
            for (PurchaseRequest request : requests) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", request.getId());
                item.put("title", request.getTitle());
                item.put("maxPrice", request.getMaxPrice());
                item.put("description", request.getDescription());
                item.put("categoryId", request.getCategoryId());
                item.put("buyerId", request.getBuyerId());
                item.put("status", request.getStatus());
                item.put("viewCount", request.getViewCount());
                item.put("createdAt", request.getCreatedAt());
                result.add(item);
            }
            return Result.success("获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户求购列表失败: " + e.getMessage());
        }
    }
}
