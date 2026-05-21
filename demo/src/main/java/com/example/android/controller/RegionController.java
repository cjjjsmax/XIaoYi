package com.example.android.controller;

import com.example.android.entity.Region;
import com.example.android.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/regions")
@CrossOrigin(origins = "*")
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping
    public Map<String, Object> getRegions(@RequestParam(required = false, defaultValue = "0") String parentId) {
        List<Region> regions = regionService.getRegionsByParentId(parentId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", regions);
        return result;
    }

    @GetMapping("/level")
    public Map<String, Object> getRegionsByLevel(@RequestParam Integer level) {
        List<Region> regions = regionService.getRegionsByLevel(level);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", regions);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getRegionById(@PathVariable Long id) {
        Region region = regionService.getRegionById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", region);
        return result;
    }
}
