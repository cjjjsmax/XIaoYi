package com.example.android.controller;

import com.example.android.common.Result;
import com.example.android.entity.Product;
import com.example.android.entity.ProductFlaw;
import com.example.android.entity.User;
import com.example.android.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private AiPromptService aiPromptService;
    @Autowired
    private ZhipuAIService zhipuAIService;
    @Autowired
    private ProductFlawService productFlawService;
    @Autowired
    private InspectionAsyncService inspectionAsyncService;

    static class PublishProductRequest {
        private Long sellerId;
        private String title;
        private String description;
        private Double price;
        private Integer categoryId;
        private Integer status;
        private String images;

        public Long getSellerId() { return sellerId; }
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Integer getCategoryId() { return categoryId; }
        public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getImages() { return images; }
        public void setImages(String images) { this.images = images; }
    }
    @PostMapping("/publish/json")
    public Result<Map<String, Object>> publishProductJson(@RequestBody PublishProductRequest request) {
        try {
            Product product = new Product();
            product.setTitle(request.getTitle());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setCategoryId(request.getCategoryId());
            product.setStatus(request.getStatus());
            product.setImages(request.getImages());
            boolean success = productService.publishProduct(product, request.getSellerId());
            if (success) {
                inspectionAsyncService.InspectionAsync(Long.valueOf(product.getId()));
                Map<String, Object> data = new HashMap<>();
                data.put("id", product.getId());
                return Result.success("发布成功，AI质检正在进行中...", data);
            } else {
                return Result.badRequest("发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发布失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload-image")
    public Result<Map<String, String>> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.badRequest("文件为空");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.badRequest("请上传图片文件");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;

            String canonicalPath;
            try {
                canonicalPath = new File("").getCanonicalPath();
            } catch (Exception e) {
                canonicalPath = System.getProperty("user.dir");
            }
            String uploadDir = canonicalPath + "/demo/uploads/images/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filePath = uploadDir + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);

            String imageUrl = "/images/" + fileName;
            Map<String, String> data = new HashMap<>();
            data.put("imageUrl", imageUrl);
            return Result.success("图片上传成功", data);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }

    private String getFullImageUrl(HttpServletRequest request, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return "";
        }
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        if ("localhost".equals(serverName) || "127.0.0.1".equals(serverName)) {
            serverName = "192.168.2.4";
        }
        
        try {
            String[] parts = imagePath.split("/");
            StringBuilder encodedPath = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    encodedPath.append("/");
                }
                encodedPath.append(URLEncoder.encode(parts[i], "UTF-8").replace("+", "%20"));
            }

            String serverUrl = request.getScheme() + "://" + serverName + ":" + serverPort;
            return serverUrl + encodedPath.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            String serverUrl = request.getScheme() + "://" + serverName + ":" + serverPort;
            return serverUrl + imagePath;
        }
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getProductList(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {

        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>().orderByDesc("created_at");
        if (categoryId != null && categoryId > 0) {
            queryWrapper.eq("category_id", categoryId);
        }

        int offset = (page - 1) * size;

        List<Product> products = productService.list(queryWrapper)
                .stream()
                .skip(offset)
                .limit(size)
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Product product : products) {
            Map<String, Object> item = new HashMap<>();
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", product.getId());
            productMap.put("title", product.getTitle());
            productMap.put("description", product.getDescription());
            productMap.put("categoryId", product.getCategoryId());
            productMap.put("price", product.getPrice());
            productMap.put("images", getFullImageUrl(request, product.getImages()));
            productMap.put("status", product.getStatus());
            productMap.put("viewCount", product.getViewCount());
            productMap.put("createdAt", product.getCreatedAt());
            productMap.put("updatedAt", product.getUpdatedAt());
            item.put("product", productMap);

            try {
                User seller = userService.getUserById(product.getSellerId());
                if (seller != null) {
                    seller.setPasswordHash(null);
                    item.put("seller", seller);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.add(item);
        }
        return Result.success(result);
    }

    @GetMapping("/search")
    public Result<List<Map<String, Object>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>()
                .like("title", keyword)
                .eq("status", 1)
                .orderByDesc("created_at");
        if (categoryId != null && categoryId > 0) {
            queryWrapper.eq("category_id", categoryId);
        }
        int offset = (page - 1) * size;
        List<Product> products = productService.list(queryWrapper)
                .stream()
                .skip(offset)
                .limit(size)
                .collect(Collectors.toList());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Product product : products) {
            Map<String, Object> item = new HashMap<>();
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", product.getId());
            productMap.put("title", product.getTitle());
            productMap.put("description", product.getDescription());
            productMap.put("categoryId", product.getCategoryId());
            productMap.put("price", product.getPrice());
            productMap.put("images", getFullImageUrl(request, product.getImages()));
            productMap.put("status", product.getStatus());
            productMap.put("viewCount", product.getViewCount());
            productMap.put("createdAt", product.getCreatedAt());
            productMap.put("updatedAt", product.getUpdatedAt());
            item.put("product", productMap);

            try {
                User seller = userService.getUserById(product.getSellerId());
                if (seller != null) {
                    seller.setPasswordHash(null);
                    item.put("seller", seller);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.add(item);
        }
        return Result.success(result);
    }

    //获取用户发布商品
    @GetMapping("/user")
    public Result<List<Map<String, Object>>> getUserProducts(@RequestParam Long sellerId, HttpServletRequest request) {
        List<Product> products = productService.list(
                new QueryWrapper<Product>()
                        .eq("seller_id", sellerId)
                        .eq("status", 1)
                        .orderByDesc("created_at")
        );
        List<Map<String, Object>> result = new ArrayList<>();
        for (Product product : products) {
            Map<String, Object> item = new HashMap<>();
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", product.getId());
            productMap.put("title", product.getTitle());
            productMap.put("description", product.getDescription());
            productMap.put("categoryId", product.getCategoryId());
            productMap.put("price", product.getPrice());
            productMap.put("images", getFullImageUrl(request, product.getImages()));
            productMap.put("status", product.getStatus());
            productMap.put("viewCount", product.getViewCount());
            productMap.put("createdAt", product.getCreatedAt());
            productMap.put("updatedAt", product.getUpdatedAt());
            item.put("product", productMap);

            try {
                User seller = userService.getUserById(product.getSellerId());
                if (seller != null) {
                    seller.setPasswordHash(null);
                    item.put("seller", seller);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.add(item);
        }
        return Result.success(result);
    }
    //编辑商品
    @PutMapping("/{productId}")
    public Result<Void> updateProduct(@PathVariable Long productId,
                                             @RequestBody Map<String,Object> request) {
        try {
            String title = (String) request.get("title");
            Double price = (Double) request.get("price");
            String description = (String) request.get("description");
            Product product = productService.getById(productId);
            if (product != null) {
                product.setTitle(title);
                product.setPrice(price);
                product.setDescription(description);
                product.setUpdatedAt(new Date());
                boolean success = productService.updateById(product);
                if (success) {
                    return Result.success("编辑成功", null);
                } else {
                    return Result.error("编辑失败");
                }
            } else {
                return Result.notFound("商品不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("编辑失败: " + e.getMessage());
        }
    }
    //删除商品
    @DeleteMapping("/delete")
    public Result<Void> deleteProduct(@RequestParam Long id) {
        try {
            Product product = productService.getById(id);
            if (product == null) {
                return Result.notFound("商品不存在");
            }
            boolean result = productService.removeById(id);
            if (result) {
                return Result.success("删除成功", null);
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    //获取商品详细
    @GetMapping("/detail")
    public Result<Map<String, Object>> getProductById(@RequestParam Long id, HttpServletRequest request) {
        try {
            Product product = productService.getById(id);
            if (product != null) {
                User seller = userService.getUserById(product.getSellerId());

                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", product.getId());
                productMap.put("title", product.getTitle());
                productMap.put("description", product.getDescription());
                productMap.put("categoryId", product.getCategoryId());
                productMap.put("price", product.getPrice());
                productMap.put("images", getFullImageUrl(request, product.getImages()));
                productMap.put("status", product.getStatus());
                productMap.put("viewCount", product.getViewCount());
                productMap.put("createdAt", product.getCreatedAt());
                productMap.put("updatedAt", product.getUpdatedAt());
                productMap.put("overallCondition", product.getOverallCondition());

                Map<String, Object> sellerMap = new HashMap<>();
                if (seller != null) {
                    sellerMap.put("id", seller.getId());
                    sellerMap.put("username", seller.getUsername());
                    sellerMap.put("phone", seller.getPhone());
                    sellerMap.put("school", seller.getSchool());
                    sellerMap.put("creditScore", seller.getCreditScore());
                }

                Map<String, Object> data = new HashMap<>();
                data.put("product", productMap);
                data.put("seller", sellerMap);

                List<ProductFlaw> flaws = productFlawService.getFlawsByProductId(id);
                List<Map<String, Object>> flawList = new ArrayList<>();
                for (ProductFlaw flaw : flaws) {
                    Map<String, Object> flawMap = new HashMap<>();
                    flawMap.put("part", flaw.getPart());
                    flawMap.put("desc", flaw.getDesc());
                    flawList.add(flawMap);
                }
                data.put("flaws", flawList);

                return Result.success("获取成功", data);
            } else {
                return Result.notFound("商品不存在");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    //获取图片列表
    private List<String> getImageUrls(HttpServletRequest request, String images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        String[] imagePaths = images.split(",");
        return Arrays.stream(imagePaths)
                .map(path -> getFullImageUrl(request, path.trim()))
                .filter(url -> !url.isEmpty())
                .toList();
    }
    // 将图片文件转换为Base64编码
    private String convertImageToBase64(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            String extension = getFileExtension(filePath);
            return "data:image/" + extension + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    // 获取文件扩展名
    private String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
            String ext = filePath.substring(dotIndex + 1).toLowerCase();
            return switch (ext) {
                case "jpg", "jpeg" -> "jpeg";
                case "png" -> "png";
                case "gif" -> "gif";
                default -> "png";
            };
        }
        return "png";
    }

    //获取分类名称
    private String getCategoryName(Integer categoryId) {
        if (categoryId == null) return "未知分类";
        return switch (categoryId) {
            case 1 -> "数码产品";
            case 2 -> "服装鞋包";
            case 3 -> "书籍文具";
            case 4 -> "运动户外";
            case 5 -> "其他";
            default -> "未知分类";
        };
    }
    // 商品质检接口（支持通过商品ID获取图片进行质检）
    @PostMapping("/inspect")
    public Map<String, Object> generateInspectionReport(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        try {
            List<String> imageBase64List = new ArrayList<>();
            String bizType = "digital";

            // 通过商品ID获取图片
            if (!request.containsKey("productId")) {
                result.put("code", 400);
                result.put("message", "缺少必要参数：productId");
                result.put("data", data);
                return result;
            }

            Long productId = ((Number) request.get("productId")).longValue();
            Product product = productService.getById(productId);
            if (product == null) {
                result.put("code", 404);
                result.put("message", "商品不存在");
                result.put("data", data);
                return result;
            }

            // 获取商品图片并转换为Base64
            String images = product.getImages();
            if (images == null || images.isEmpty()) {
                result.put("code", 400);
                result.put("message", "商品没有图片");
                result.put("data", data);
                return result;
            }
            
            String[] imagePaths = images.split(",");
            String uploadDir = "demo/uploads/";
            for (String imagePath : imagePaths) {
                String trimmedPath = imagePath.trim();
                if (!trimmedPath.isEmpty()) {
                    String cleanPath = trimmedPath;
                    if (cleanPath.startsWith("/")) {
                        cleanPath = cleanPath.substring(1);
                    }
                    String fullPath = uploadDir + cleanPath;
                    String base64 = convertImageToBase64(fullPath);
                    if (base64 != null) {
                        imageBase64List.add(base64);
                    }
                }
            }
            
            if (imageBase64List.isEmpty()) {
                result.put("code", 400);
                result.put("message", "无法读取图片文件");
                result.put("data", data);
                return result;
            }

            // 获取分类对应的提示词
            bizType = aiPromptService.getBizTypeByCategoryId(product.getCategoryId());

            // 获取质检提示词模板
            String prompt = aiPromptService.getProductInspectionPrompt(bizType);

            // 调用AI生成报告（使用Base64图片）
            String reportContent = zhipuAIService.generateProductReportWithBase64(prompt, imageBase64List);

            // 解析报告
            Map<String, Object> parsedReport = zhipuAIService.parseInspectionReport(reportContent);
            String overallCondition = (String) parsedReport.get("overall_condition");
            List<Map<String, String>> flaws = (List<Map<String, String>>) parsedReport.get("flaws");

            // 保存质检结果到数据库
            if (overallCondition != null && !overallCondition.isEmpty()) {
                product.setOverallCondition(overallCondition);
                productService.updateById(product);
            }
            productFlawService.deleteFlawsByProductId(productId);
            if (flaws != null && !flaws.isEmpty()) {
                productFlawService.saveFlaws(productId, flaws);
            }

            // 构建响应
            result.put("code", 200);
            result.put("message", "success");
            data.put("overall_condition", overallCondition);
            data.put("flaws", flaws != null ? flaws : List.of());
            result.put("data", data);

        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "AI服务调用失败: " + e.getMessage());
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "生成报告失败: " + e.getMessage());
            result.put("data", data);
        }

        return result;
    }
}
