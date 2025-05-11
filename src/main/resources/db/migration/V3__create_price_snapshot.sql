CREATE TABLE price_snapshot (
    store_id BIGINT NOT NULL,
    product_id VARCHAR(20) NOT NULL,
    snapshot_date DATE NOT NULL,
    package_quantity DECIMAL(10,3) NOT NULL,
    package_unit VARCHAR(10) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'RON',
    PRIMARY KEY (store_id, product_id, snapshot_date),
    CONSTRAINT fk_ps_store FOREIGN KEY (store_id) REFERENCES store(id),
    CONSTRAINT fk_ps_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);
