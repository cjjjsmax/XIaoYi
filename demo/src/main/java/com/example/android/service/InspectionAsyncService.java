package com.example.android.service;

import com.example.android.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InspectionAsyncService {
    @Autowired
    private ProductService productService;

    @Autowired
    private ZhipuAIService zhipuAIService;

    @Autowired
    private ProductFlawService productFlawService;
    @Autowired
    private AiPromptService aiPromptService;

    @Async("taskExecutor")
    public void InspectionAsync(Long productId) {
        log.info("========== 开始异步质检 ==========");
        log.info("商品ID: {}", productId);

        try {
            //获取商品信息
            Product product = productService.getById(productId);
            if (product == null) {
                log.error("商品不存在，ID: {}", productId);
                return;
            }
             //获取图片Base64列表
            List<String> imageBase64List = new ArrayList<>();
            String[] imagePaths = product.getImages().split(",");
            for (String imagePath : imagePaths) {
                String filePath = getFullFilePath(imagePath.trim());
                String base64 = convertImageToBase64(filePath);
                if (base64 != null) {
                    imageBase64List.add(base64);
                }
            }

            if (imageBase64List.isEmpty()) {
                log.error("无法读取任何图片文件");
                return;
            }

            //获取提示词
            String bizType = aiPromptService.getBizTypeByCategoryId(product.getCategoryId());
            String prompt = aiPromptService.getProductInspectionPrompt(bizType);
            log.info("提示词长度: {}", prompt != null ? prompt.length() : 0);

            //调用AI服务
            log.info("开始调用AI服务...");
            String aiResponse = zhipuAIService.generateProductReportWithBase64(prompt, imageBase64List);

            //解析AI响应
            Map<String, Object> inspectionResult = zhipuAIService.parseInspectionReport(aiResponse);
            String overallCondition = (String) inspectionResult.get("overall_condition");
            @SuppressWarnings("unchecked")
            List<Map<String, String>> flaws = (List<Map<String, String>>) inspectionResult.get("flaws");

            //更新商品成色
            if (overallCondition != null) {
                product.setOverallCondition(overallCondition);
                productService.updateById(product);
            }

            //保存瑕疵记录
            if (flaws != null && !flaws.isEmpty()) {
                productFlawService.deleteFlawsByProductId(productId);
                productFlawService.saveFlaws(productId, flaws);
            }

            log.info("========== 异步质检完成 ==========");

        } catch (Exception e) {
            log.error("异步质检失败，商品ID: {}, 错误: {}", productId, e.getMessage());
            e.printStackTrace();
        }
    }

    //获取完整文件路径
    private String getFullFilePath(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return "";
        }
        String canonicalPath;
        try {
            canonicalPath = new File("").getCanonicalPath();
        } catch (Exception e) {
            canonicalPath = System.getProperty("user.dir");
        }
        return canonicalPath + "/demo/uploads/" + imagePath;
    }

    //图片转Base64
    private String convertImageToBase64(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("图片文件不存在: {}", filePath);
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            String extension = getFileExtension(filePath);
            //返回Data URI格式
            return "data:image/" + extension + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("读取图片文件失败: {}", filePath);
            return null;
        }
    }

    //获取文件扩展名
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
}
