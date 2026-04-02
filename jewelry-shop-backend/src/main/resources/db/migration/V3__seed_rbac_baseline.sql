INSERT INTO roles (created_at, updated_at, name, description)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ROLE_ADMIN', 'System administrator role'
WHERE NOT EXISTS (
    SELECT 1 FROM roles r WHERE r.name = 'ROLE_ADMIN'
);

INSERT INTO roles (created_at, updated_at, name, description)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ROLE_USER', 'Default customer role'
WHERE NOT EXISTS (
    SELECT 1 FROM roles r WHERE r.name = 'ROLE_USER'
);

INSERT INTO permissions (created_at, updated_at, name, description)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CATEGORY_MANAGE', 'Manage categories'
WHERE NOT EXISTS (
    SELECT 1 FROM permissions p WHERE p.name = 'CATEGORY_MANAGE'
);

INSERT INTO permissions (created_at, updated_at, name, description)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'PRODUCT_MANAGE', 'Manage products'
WHERE NOT EXISTS (
    SELECT 1 FROM permissions p WHERE p.name = 'PRODUCT_MANAGE'
);

INSERT INTO permissions (created_at, updated_at, name, description)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ORDER_MANAGE', 'Manage orders'
WHERE NOT EXISTS (
    SELECT 1 FROM permissions p WHERE p.name = 'ORDER_MANAGE'
);

INSERT INTO permissions (created_at, updated_at, name, description)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ORDER_VIEW', 'View own orders'
WHERE NOT EXISTS (
    SELECT 1 FROM permissions p WHERE p.name = 'ORDER_VIEW'
);

INSERT INTO permissions (created_at, updated_at, name, description)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CART_MANAGE', 'Manage own cart'
WHERE NOT EXISTS (
    SELECT 1 FROM permissions p WHERE p.name = 'CART_MANAGE'
);

INSERT INTO role_permissions (created_at, updated_at, role_id, permission_id, assigned_by)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, r.id, p.id, NULL
FROM roles r
JOIN permissions p ON p.name IN ('CATEGORY_MANAGE', 'PRODUCT_MANAGE', 'ORDER_MANAGE', 'ORDER_VIEW', 'CART_MANAGE')
WHERE r.name = 'ROLE_ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM role_permissions rp
      WHERE rp.role_id = r.id
        AND rp.permission_id = p.id
  );

INSERT INTO role_permissions (created_at, updated_at, role_id, permission_id, assigned_by)
SELECT CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, r.id, p.id, NULL
FROM roles r
JOIN permissions p ON p.name IN ('ORDER_VIEW', 'CART_MANAGE')
WHERE r.name = 'ROLE_USER'
  AND NOT EXISTS (
      SELECT 1
      FROM role_permissions rp
      WHERE rp.role_id = r.id
        AND rp.permission_id = p.id
  );
