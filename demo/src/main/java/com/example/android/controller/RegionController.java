package com.example.android.controller;

import com.example.android.common.Result;
import com.example.android.entity.Region;
import com.example.android.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@CrossOrigin(origins = "*")
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping
    public Result<List<Region>> getRegions(@RequestParam(required = false, defaultValue = "0") String parentId) {
        try {
            List<Region> regions = regionService.getRegionsByParentId(parentId);
            return Result.success("获取成功", regions);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取地区列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/level")
    public Result<List<Region>> getRegionsByLevel(@RequestParam Integer level) {
        try {
            List<Region> regions = regionService.getRegionsByLevel(level);
            return Result.success("获取成功", regions);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取地区列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Region> getRegionById(@PathVariable Long id) {
        try {
            Region region = regionService.getRegionById(id);
            if (region != null) {
                return Result.success("获取成功", region);
            } else {
                return Result.notFound("地区不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取地区失败: " + e.getMessage());
        }
    }
}
