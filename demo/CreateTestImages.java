import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateTestImages {
    public static void main(String[] args) throws IOException {
        // 有效的最小PNG文件数据
        byte[] pngData = {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x64, 0x00, 0x00, 0x00, 0x64,
            0x08, 0x06, 0x00, 0x00, 0x00, (byte)0x73, (byte)0x7A, (byte)0x7A, (byte)0xF4,
            0x00, 0x00, 0x00, 0x01, 0x73, 0x52, 0x47, 0x42,
            0x00, (byte)0xAE, (byte)0xCE, 0x1C, (byte)0xE9,
            0x00, 0x00, 0x00, 0x44, 0x49, 0x44, 0x41, 0x54,
            0x48, (byte)0xC7, (byte)0xED, (byte)0xC1, 0x01, 0x01, 0x00, 0x00,
            0x00, (byte)0x82, 0x20, (byte)0xFF, (byte)0xAF, (byte)0xA8, 0x3B, 0x1E,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            (byte)0x80, (byte)0x82, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01,
            0x5D, 0x0A, 0x2B, (byte)0xB0,
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
            (byte)0xAE, 0x42, 0x60, (byte)0x82
        };

        String[] filenames = {
            "7cd25fe5-9792-4d4f-92f3-6678fbd6de44_屏幕截图 2026-01-16 200847.png",
            "7b325452-b2f7-4472-a714-f1101e9e6754_屏幕截图 2026-01-31 001045.png",
            "3aabd780-3caf-4cdb-af4e-41215c616aa8_屏幕截图 2026-03-12 181516.png",
            "14fa78e0-4736-4524-9428-d36baa0fb463_屏幕截图 2026-03-22 201610.png",
            "f169fa48-8e06-447f-a5c8-0585cf7002d2_屏幕截图 2025-07-16 181647.png",
            "7e3f2770-3e78-41f1-820a-fbfa9d74f3ad_屏幕截图 2025-07-16 181701.png",
            "fde3451d-9cca-4a61-a3f6-b869cb8afb4b_屏幕截图 2025-07-16 181722.png"
        };

        File dir = new File("uploads/images");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (String filename : filenames) {
            File file = new File(dir, filename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pngData);
                System.out.println("Created: " + filename);
            }
        }

        System.out.println("All test images created successfully!");
    }
}
