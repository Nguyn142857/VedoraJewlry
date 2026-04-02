DELETE FROM cart_items
WHERE id IN (
    SELECT id
    FROM (
        SELECT id,
               ROW_NUMBER() OVER (
                   PARTITION BY cart_id, product_variant_id
                   ORDER BY id
               ) AS rn
        FROM cart_items
    ) duplicate_rows
    WHERE duplicate_rows.rn > 1
);

ALTER TABLE cart_items
    ADD CONSTRAINT uk_cart_items_cart_variant UNIQUE (cart_id, product_variant_id);
