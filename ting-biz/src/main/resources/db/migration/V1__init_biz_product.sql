CREATE TABLE biz_product (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(128) NOT NULL,
    price       DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock       INT          NOT NULL DEFAULT 0,
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

INSERT INTO biz_product (name, price, stock, status) VALUES
('示例商品 A', 99.00, 100, 1),
('示例商品 B', 199.00, 50, 1);
