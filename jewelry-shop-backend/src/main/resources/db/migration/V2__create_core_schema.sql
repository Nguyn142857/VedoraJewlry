CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NULL,
    address VARCHAR(255) NULL,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uk_permissions_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_by BIGINT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (id),
    CONSTRAINT uk_user_roles_user_role UNIQUE (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_user_roles_assigned_by FOREIGN KEY (assigned_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    assigned_by BIGINT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (id),
    CONSTRAINT uk_role_permissions_role_permission UNIQUE (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id),
    CONSTRAINT fk_role_permissions_assigned_by FOREIGN KEY (assigned_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    description TEXT NULL,
    status VARCHAR(20) NOT NULL,
    version BIGINT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uk_category_name UNIQUE (name),
    CONSTRAINT uk_category_slug UNIQUE (slug)
);

CREATE INDEX IF NOT EXISTS idx_category_slug ON categories(slug);
CREATE INDEX IF NOT EXISTS idx_category_status ON categories(status);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(180) NOT NULL,
    description TEXT NULL,
    material VARCHAR(100) NULL,
    base_price DECIMAL(15,2) NOT NULL,
    thumbnail VARCHAR(255) NULL,
    status BIT NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT uk_products_slug UNIQUE (slug),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX IF NOT EXISTS idx_products_category_id ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);

CREATE TABLE IF NOT EXISTS product_variants (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    product_id BIGINT NOT NULL,
    sku VARCHAR(100) NOT NULL,
    size VARCHAR(50) NULL,
    color VARCHAR(50) NULL,
    gemstone VARCHAR(100) NULL,
    price DECIMAL(15,2) NOT NULL,
    stock_quantity INT NOT NULL,
    deleted BIT NOT NULL,
    status BIT NOT NULL,
    CONSTRAINT pk_product_variants PRIMARY KEY (id),
    CONSTRAINT uk_product_variants_sku UNIQUE (sku),
    CONSTRAINT fk_product_variants_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX IF NOT EXISTS idx_product_variants_product_id ON product_variants(product_id);
CREATE INDEX IF NOT EXISTS idx_product_variants_status ON product_variants(status);
CREATE INDEX IF NOT EXISTS idx_product_variants_deleted ON product_variants(deleted);

CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    is_main BIT NOT NULL,
    deleted BIT NOT NULL,
    sort_order INT NULL,
    CONSTRAINT pk_product_images PRIMARY KEY (id),
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images(product_id);
CREATE INDEX IF NOT EXISTS idx_product_images_is_main ON product_images(is_main);
CREATE INDEX IF NOT EXISTS idx_product_images_deleted ON product_images(deleted);

CREATE TABLE IF NOT EXISTS carts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_carts PRIMARY KEY (id),
    CONSTRAINT uk_carts_user_id UNIQUE (user_id),
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    cart_id BIGINT NOT NULL,
    product_variant_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    CONSTRAINT pk_cart_items PRIMARY KEY (id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT fk_cart_items_variant FOREIGN KEY (product_variant_id) REFERENCES product_variants(id)
);

CREATE INDEX IF NOT EXISTS idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_variant_id ON cart_items(product_variant_id);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    user_id BIGINT NOT NULL,
    order_code VARCHAR(50) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    shipping_fee DECIMAL(15,2) NULL,
    discount_amount DECIMAL(15,2) NULL,
    final_amount DECIMAL(15,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    order_status VARCHAR(50) NOT NULL,
    receiver_name VARCHAR(100) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    note VARCHAR(255) NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT uk_orders_order_code UNIQUE (order_code),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_order_status ON orders(order_status);
CREATE INDEX IF NOT EXISTS idx_orders_payment_status ON orders(payment_status);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    order_id BIGINT NOT NULL,
    product_variant_id BIGINT NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    variant_info VARCHAR(255) NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_variant FOREIGN KEY (product_variant_id) REFERENCES product_variants(id)
);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_variant_id ON order_items(product_variant_id);
