SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM order_items WHERE id IN (8001, 8002);
DELETE FROM orders WHERE id IN (7001);
DELETE FROM cart_items WHERE id IN (6101, 6102);
DELETE FROM carts WHERE id IN (6001, 6002);
DELETE FROM product_images WHERE id IN (5001, 5002, 5003, 5004, 5005, 5006, 5007, 5008);
DELETE FROM product_variants WHERE id IN (4001, 4002, 4003, 4004, 4005, 4006, 4007);
DELETE FROM products WHERE id IN (3001, 3002, 3003, 3004);
DELETE FROM categories WHERE id IN (2001, 2002, 2003);
DELETE FROM user_roles WHERE id IN (1101, 1102);
DELETE FROM users WHERE id IN (1001, 1002);

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (id, created_at, updated_at, full_name, email, password, phone, address, status)
VALUES
    (1001, NOW(), NOW(), 'Nguyen Thi An', 'user@example.com', '$2a$10$qW/Wimn6T3bQEODf.eI84OugJGDL0S8ckCqWB0P98ZuM5ZeTeBGw2', '0901234567', '12 Nguyen Hue, Quan 1, TP.HCM', 'ACTIVE'),
    (1002, NOW(), NOW(), 'Vedora Admin', 'admin@vedora.local', '$2a$10$JfcKT53weiWhZsp/JMdZ2e4OseBU7W10yDsRPrBRTi6j4sAXhw3ge', '0988888888', '1 Le Loi, Quan 1, TP.HCM', 'ACTIVE');

INSERT INTO user_roles (id, created_at, updated_at, user_id, role_id, assigned_by)
SELECT 1101, NOW(), NOW(), 1001, r.id, NULL
FROM roles r
WHERE r.name = 'ROLE_USER';

INSERT INTO user_roles (id, created_at, updated_at, user_id, role_id, assigned_by)
SELECT 1102, NOW(), NOW(), 1002, r.id, 1002
FROM roles r
WHERE r.name = 'ROLE_ADMIN';

INSERT INTO categories (id, created_at, updated_at, name, slug, description, status, version)
VALUES
    (2001, NOW(), NOW(), 'Nhan', 'nhan', 'Bo suu tap nhan thanh lich cho su dung hang ngay va qua tang.', 'ACTIVE', 0),
    (2002, NOW(), NOW(), 'Day chuyen', 'day-chuyen', 'Nhung mau day chuyen thanh lich, toi gian va de phoi do.', 'ACTIVE', 0),
    (2003, NOW(), NOW(), 'Bong tai', 'bong-tai', 'Bong tai cao cap cho phong cach hien dai.', 'ACTIVE', 0);

INSERT INTO products (id, created_at, updated_at, name, slug, description, material, base_price, thumbnail, status, category_id)
VALUES
    (3001, NOW(), NOW(), 'Nhan Bac Moissanite Aurora', 'nhan-bac-moissanite-aurora', 'Nhan bac 925 gan da moissanite, phong cach thanh lich.', 'Bac 925', 1290000.00, 'https://images.unsplash.com/photo-1617038260897-41a1f14a8ca0?auto=format&fit=crop&w=900&q=80', b'1', 2001),
    (3002, NOW(), NOW(), 'Nhan Vang Trang Celeste', 'nhan-vang-trang-celeste', 'Nhan vang trang thiet ke toi gian, hop dung hang ngay.', 'Vang trang', 2490000.00, 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=900&q=80', b'1', 2001),
    (3003, NOW(), NOW(), 'Day Chuyen Pearl Drop', 'day-chuyen-pearl-drop', 'Day chuyen ngoc trai nhe, thanh lich cho da hoi va cong so.', 'Bac ma vang', 1890000.00, 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=900&q=80', b'1', 2002),
    (3004, NOW(), NOW(), 'Bong Tai Golden Leaf', 'bong-tai-golden-leaf', 'Bong tai dang la, tong mau vang champagne.', 'Hop kim cao cap', 990000.00, 'https://images.unsplash.com/photo-1635767798638-3e25273a8236?auto=format&fit=crop&w=900&q=80', b'1', 2003);

INSERT INTO product_variants (id, created_at, updated_at, product_id, sku, size, color, gemstone, price, stock_quantity, deleted, status)
VALUES
    (4001, NOW(), NOW(), 3001, 'AURORA-R6', '6', 'Silver', 'Moissanite', 1290000.00, 12, b'0', b'1'),
    (4002, NOW(), NOW(), 3001, 'AURORA-R7', '7', 'Silver', 'Moissanite', 1290000.00, 8, b'0', b'1'),
    (4003, NOW(), NOW(), 3002, 'CELESTE-R6', '6', 'White Gold', NULL, 2490000.00, 5, b'0', b'1'),
    (4004, NOW(), NOW(), 3002, 'CELESTE-R7', '7', 'White Gold', NULL, 2490000.00, 3, b'0', b'1'),
    (4005, NOW(), NOW(), 3003, 'PEARL-ONE', NULL, 'Gold', 'Pearl', 1890000.00, 10, b'0', b'1'),
    (4006, NOW(), NOW(), 3004, 'GLEAF-STD', NULL, 'Champagne Gold', NULL, 990000.00, 15, b'0', b'1'),
    (4007, NOW(), NOW(), 3004, 'GLEAF-ROSE', NULL, 'Rose Gold', NULL, 1090000.00, 9, b'0', b'1');

INSERT INTO product_images (id, created_at, updated_at, product_id, image_url, is_main, deleted, sort_order)
VALUES
    (5001, NOW(), NOW(), 3001, 'https://images.unsplash.com/photo-1617038260897-41a1f14a8ca0?auto=format&fit=crop&w=900&q=80', b'1', b'0', 1),
    (5002, NOW(), NOW(), 3001, 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=80', b'0', b'0', 2),
    (5003, NOW(), NOW(), 3002, 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=900&q=80', b'1', b'0', 1),
    (5004, NOW(), NOW(), 3002, 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=900&q=80', b'0', b'0', 2),
    (5005, NOW(), NOW(), 3003, 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=900&q=80', b'1', b'0', 1),
    (5006, NOW(), NOW(), 3003, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?auto=format&fit=crop&w=900&q=80', b'0', b'0', 2),
    (5007, NOW(), NOW(), 3004, 'https://images.unsplash.com/photo-1635767798638-3e25273a8236?auto=format&fit=crop&w=900&q=80', b'1', b'0', 1),
    (5008, NOW(), NOW(), 3004, 'https://images.unsplash.com/photo-1617038220319-276d3cfab638?auto=format&fit=crop&w=900&q=80', b'0', b'0', 2);

INSERT INTO carts (id, created_at, updated_at, user_id)
VALUES
    (6001, NOW(), NOW(), 1001),
    (6002, NOW(), NOW(), 1002);

INSERT INTO cart_items (id, created_at, updated_at, cart_id, product_variant_id, quantity, unit_price)
VALUES
    (6101, NOW(), NOW(), 6001, 4001, 1, 1290000.00),
    (6102, NOW(), NOW(), 6001, 4005, 2, 1890000.00);

INSERT INTO orders (
    id, created_at, updated_at, user_id, order_code, total_amount, shipping_fee, discount_amount, final_amount,
    payment_method, payment_status, order_status, receiver_name, receiver_phone, receiver_address, note
)
VALUES
    (
        7001, NOW(), NOW(), 1001, 'VDR-10001', 3180000.00, 30000.00, 0.00, 3210000.00,
        'COD', 'UNPAID', 'PENDING', 'Nguyen Thi An', '0901234567', '12 Nguyen Hue, Quan 1, TP.HCM',
        'Giao gio hanh chinh'
    );

INSERT INTO order_items (id, created_at, updated_at, order_id, product_variant_id, product_name, variant_info, quantity, unit_price, subtotal)
VALUES
    (8001, NOW(), NOW(), 7001, 4001, 'Nhan Bac Moissanite Aurora', 'Size: 6, Color: Silver, Gemstone: Moissanite', 1, 1290000.00, 1290000.00),
    (8002, NOW(), NOW(), 7001, 4005, 'Day Chuyen Pearl Drop', 'Color: Gold, Gemstone: Pearl', 1, 1890000.00, 1890000.00);
