-- ========================================
-- 堆堆仓库管理系统 - 本地数据库初始化脚本
-- 使用方式: mysql -u root -p < init.sql
-- ========================================

CREATE DATABASE IF NOT EXISTS duidui DEFAULT CHARACTER SET utf8mb4;
USE duidui;

-- ========================================
-- 1. 仓库表
-- ========================================
CREATE TABLE `warehouse` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
  `name`        VARCHAR(100) DEFAULT NULL            COMMENT '仓库名称',
  `location`    VARCHAR(200) DEFAULT NULL            COMMENT '仓库地址',
  `manager`     VARCHAR(100) DEFAULT NULL            COMMENT '仓库负责人',
  `created_at`  DATETIME     DEFAULT NULL            COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库';

-- ========================================
-- 2. 商品表
-- ========================================
CREATE TABLE `product` (
  `id`                  BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name`                VARCHAR(100) NOT NULL            COMMENT '商品名称',
  `category`            VARCHAR(100) DEFAULT NULL        COMMENT '商品分类',
  `sku`                 VARCHAR(100) DEFAULT NULL        COMMENT '商品编码',
  `unit`                VARCHAR(20)  DEFAULT NULL        COMMENT '单位：件/箱/袋',
  `price`               DECIMAL(10,2) DEFAULT NULL       COMMENT '单价',
  `low_stock_threshold` INT(11)      DEFAULT '0'         COMMENT '库存预警阈值',
  `status`              TINYINT(4)   DEFAULT '1'         COMMENT '状态：0停用 1启用',
  `description`         TEXT                              COMMENT '商品描述',
  `created_at`          DATETIME     DEFAULT NULL        COMMENT '创建时间',
  `updated_at`          DATETIME     DEFAULT NULL        COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku` (`sku`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品';

-- ========================================
-- 3. 库存表（含乐观锁）
-- ========================================
CREATE TABLE `stock` (
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `product_id`   BIGINT(20) NOT NULL            COMMENT '商品ID',
  `warehouse_id` BIGINT(20) DEFAULT '1'         COMMENT '仓库ID',
  `quantity`     INT(11)    NOT NULL DEFAULT '0' COMMENT '当前库存数量',
  `version`      INT(11)    DEFAULT '0'         COMMENT '乐观锁版本号',
  `update_time`  DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_warehouse` (`product_id`, `warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存';

-- ========================================
-- 4. 用户表
-- ========================================
CREATE TABLE `user` (
  `id`         BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`   VARCHAR(50) NOT NULL            COMMENT '用户名',
  `password`   VARCHAR(100) NOT NULL           COMMENT '密码（BCrypt加密）',
  `role`       TINYINT(4)  DEFAULT 0           COMMENT '角色：1管理员 0普通用户',
  `created_at` DATETIME    DEFAULT NULL        COMMENT '创建时间',
  `updated_at` DATETIME    DEFAULT NULL        COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

-- ========================================
-- 5. 入库单主表
-- ========================================
CREATE TABLE `inbound` (
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '入库单ID',
  `inbound_no`     VARCHAR(100) DEFAULT NULL        COMMENT '入库单号',
  `supplier`       VARCHAR(100) DEFAULT NULL        COMMENT '供应商',
  `total_quantity` INT(11)      DEFAULT NULL        COMMENT '总数量',
  `status`         VARCHAR(20)  DEFAULT 'PENDING'   COMMENT '状态：PENDING/COMPLETED',
  `warehouse_id`   BIGINT(20)   DEFAULT '1'         COMMENT '仓库ID',
  `operator_id`    BIGINT(20)   NOT NULL            COMMENT '操作人ID',
  `remark`         VARCHAR(500) DEFAULT NULL        COMMENT '备注',
  `created_at`     DATETIME     DEFAULT NULL        COMMENT '创建时间',
  `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inbound_no` (`inbound_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单';

-- ========================================
-- 6. 入库明细表
-- ========================================
CREATE TABLE `inbound_detail` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `inbound_id`  BIGINT(20)   NOT NULL            COMMENT '入库单ID',
  `product_id`  BIGINT(20)   NOT NULL            COMMENT '商品ID',
  `quantity`    INT(11)      NOT NULL            COMMENT '入库数量',
  `price`       DECIMAL(10,2) DEFAULT NULL       COMMENT '入库单价',
  PRIMARY KEY (`id`),
  KEY `idx_inbound_id` (`inbound_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库明细';

-- ========================================
-- 7. 出库单主表
-- ========================================
CREATE TABLE `outbound` (
  `id`             BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '出库单ID',
  `outbound_no`    VARCHAR(100) DEFAULT NULL       COMMENT '出库单号',
  `customer`       VARCHAR(100) DEFAULT NULL       COMMENT '客户名称',
  `total_quantity` INT(11)     DEFAULT NULL        COMMENT '总数量',
  `status`         VARCHAR(20) DEFAULT 'PENDING'   COMMENT '状态：PENDING/COMPLETED',
  `created_at`     DATETIME    DEFAULT NULL        COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_outbound_no` (`outbound_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单';

-- ========================================
-- 8. 出库明细表
-- ========================================
CREATE TABLE `outbound_item` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `outbound_id` BIGINT(20) DEFAULT NULL            COMMENT '出库单ID',
  `item_id`     BIGINT(20) DEFAULT NULL            COMMENT '商品ID',
  `quantity`    INT(11)    DEFAULT NULL            COMMENT '出库数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库明细';

-- ========================================
-- 默认数据
-- ========================================
INSERT IGNORE INTO warehouse (id, name, location, manager, created_at)
VALUES (1, '默认仓库', '本地仓库', '管理员', NOW());
