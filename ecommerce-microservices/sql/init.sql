-- ================================================================
-- 电商微服务系统 · 初始化 SQL 脚本
-- 使用前请确保 MySQL 已启动，执行以下命令：
--   mysql -u root -p < init.sql
-- ================================================================

-- 创建数据库（如果不存在则新建）
CREATE DATABASE IF NOT EXISTS ecommerce
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE ecommerce;

-- ======================== 商品表 ========================
CREATE TABLE IF NOT EXISTS product (
    id          BIGINT          NOT NULL PRIMARY KEY COMMENT '商品 ID',
    name        VARCHAR(128)    NOT NULL COMMENT '商品名称',
    price       DECIMAL(10, 2)  NOT NULL DEFAULT 0.00 COMMENT '商品价格（元）',
    stock       INT             NOT NULL DEFAULT 0 COMMENT '库存数量',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品表';

-- 插入测试数据
INSERT INTO product (id, name, price, stock) VALUES
    (101, 'iPhone 15 Pro', 7999.00, 100);

-- ======================== 订单表 ========================
CREATE TABLE IF NOT EXISTS order_info (
    id          BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    order_no    VARCHAR(32)     NOT NULL COMMENT '订单编号（业务唯一）',
    product_id  BIGINT          NOT NULL COMMENT '商品 ID',
    amount      DECIMAL(10, 2)  NOT NULL DEFAULT 0.00 COMMENT '订单金额（元）',
    status      TINYINT         NOT NULL DEFAULT 0 COMMENT '订单状态：0-已创建，1-已支付，2-已发货，3-已完成',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_no (order_no)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单表';
