package com.jewelry.jewelryshopbackend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 1. Mật khẩu bạn muốn kiểm tra
        String rawPassword = "123456";

        // 2. Chuỗi Hash bạn đã INSERT vào database (từ câu lệnh SQL trước)
        String hashInDatabase = "$2a$10$7p6shT8.t9S8S.B5FfXN0n9S9S9OQf5m6zJp5zKq.X5r5/S7S5S5.";

        // 3. Thực hiện kiểm tra
        boolean isMatch = encoder.matches(rawPassword, hashInDatabase);

        System.out.println("=== KIỂM TRA MẬT KHẨU TỪ DATABASE ===");
        System.out.println("Mật khẩu nhập vào: " + rawPassword);
        System.out.println("Hash trong DB    : " + hashInDatabase);
        System.out.println("-------------------------------------");

        if (isMatch) {
            System.out.println("✅ KẾT QUẢ: Mật khẩu chính xác! Bạn có thể dùng hash này.");
        } else {
            System.out.println("❌ KẾT QUẢ: Không khớp. Vui lòng kiểm tra lại chuỗi Hash.");
        }

        // Tạo thêm một hash mới ngẫu nhiên để bạn so sánh (nếu muốn)
        System.out.println("\nHash mới tạo ngẫu nhiên (vẫn là 123456): ");
        System.out.println(encoder.encode(rawPassword));
    }
}