CREATE TABLE discount (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  product_id VARCHAR(20)  NOT NULL,
  from_date DATE NOT NULL,
  to_date DATE NOT NULL,
  percentage_of_discount DECIMAL(5,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_disc_store FOREIGN KEY (store_id) REFERENCES store(id),
  CONSTRAINT fk_disc_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);
