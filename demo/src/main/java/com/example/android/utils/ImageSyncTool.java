package com.example.android.utils;

import com.example.android.entity.Product;
import com.example.android.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * 图片同步工具类
 * 用于同步数据库中记录的图片文件到指定目录
 */
@Component
public class ImageSyncTool {

    @Autowired
    private ProductService productService;

    /**
     * 目标目录（图片最终存储位置）
     */
    private static final String TARGET_DIR = "uploads/images/";

    /**
     * 可能的源目录列表（需要根据实际情况配置）
     */
    private static final String[] POSSIBLE_SOURCE_DIRS = {
            "src/main/resources/static/images/",  // 资源目录
            "images/",                            // 根目录下的images
            "backup/images/",                     // 备份目录
            "product_images/",                    // 产品图片目录
            "../images/",                         // 上级目录
            "D:/images/",                         // Windows常见路径
            "E:/product_images/"                  // Windows常见路径
    };

    /**
     * 应用启动时自动执行同步
     */
    @PostConstruct
    public void syncImagesOnStartup() {
        System.out.println("========== 开始同步商品图片 ==========");
        
        // 确保目标目录存在
        File target = new File(TARGET_DIR);
        if (!target.exists()) {
            target.mkdirs();
            System.out.println("创建目标目录: " + TARGET_DIR);
        }

        // 获取数据库中所有商品的图片路径
        List<Product> products = productService.list();
        
        int found = 0;
        int copied = 0;
        int missing = 0;

        for (Product product : products) {
            String imagePath = product.getImages();
            if (imagePath == null || imagePath.isEmpty()) {
                continue;
            }

            // 提取文件名（去掉 /images/ 前缀）
            String fileName = imagePath;
            if (fileName.startsWith("/images/")) {
                fileName = fileName.substring(8);
            }

            // 检查目标目录是否已存在
            File targetFile = new File(TARGET_DIR + fileName);
            if (targetFile.exists()) {
                found++;
                continue;
            }

            // 尝试从各个可能的源目录复制
            boolean copiedSuccess = false;
            for (String sourceDir : POSSIBLE_SOURCE_DIRS) {
                File sourceFile = new File(sourceDir + fileName);
                if (sourceFile.exists()) {
                    try {
                        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        copied++;
                        copiedSuccess = true;
                        System.out.println("✓ 已复制: " + fileName + " (来源: " + sourceDir + ")");
                        break;
                    } catch (Exception e) {
                        System.out.println("✗ 复制失败: " + fileName + " - " + e.getMessage());
                    }
                }
            }

            if (!copiedSuccess) {
                missing++;
                System.out.println("✗ 缺失文件: " + fileName);
            }
        }

        System.out.println("========== 图片同步完成 ==========");
        System.out.println("已存在: " + found + " 个");
        System.out.println("已复制: " + copied + " 个");
        System.out.println("缺失: " + missing + " 个");
        
        if (missing > 0) {
            System.out.println("提示：请将缺失的图片文件手动复制到 " + TARGET_DIR + " 目录");
        }
    }
}
