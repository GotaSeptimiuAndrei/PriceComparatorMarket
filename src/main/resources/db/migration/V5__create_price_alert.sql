CREATE TABLE price_alert (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     user_email VARCHAR(100) NOT NULL,
     product_id VARCHAR(20)  NOT NULL,
     store_id BIGINT NULL, -- null = any store
     target_price DECIMAL(10,2) NOT NULL,
     currency CHAR(3) NOT NULL DEFAULT 'RON',
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE price_alert_trigger (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     alert_id BIGINT NOT NULL,
     snapshot_date DATE NOT NULL,
     hit_price DECIMAL(10,2) NOT NULL,
     store_name VARCHAR(100) NOT NULL,
     FOREIGN KEY (alert_id) REFERENCES price_alert(id)
);

-- Fast lookup: for a given product (and maybe store) find all active alerts ≤ price
CREATE INDEX idx_active_alert
    ON price_alert (product_id, store_id, target_price, active);
