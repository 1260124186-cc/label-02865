SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- PhoneMall 手机商城 建表脚本
-- MySQL 8.0+

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    `nickname` VARCHAR(50) DEFAULT '' COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT '' COMMENT '头像',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '0普通用户 1管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(255) DEFAULT '' COMMENT '图标URL',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `price` DECIMAL(10,2) NOT NULL COMMENT '售价',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `main_image` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '主图',
    `images` TEXT COMMENT '图片列表(JSON)',
    `description` TEXT COMMENT '商品描述(HTML)',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0下架 1上架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品表';

-- 轮播图表
CREATE TABLE IF NOT EXISTS `banner` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `product_id` BIGINT DEFAULT NULL COMMENT '关联商品ID',
    `sort_order` INT NOT NULL DEFAULT 0,
    `status` TINYINT NOT NULL DEFAULT 1,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='轮播图表';

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL DEFAULT 1,
    `selected` TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='购物车表';

-- 收货地址表
CREATE TABLE IF NOT EXISTS `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货电话',
    `province` VARCHAR(50) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `district` VARCHAR(50) NOT NULL,
    `detail` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收货地址表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL,
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0待付款 1待发货 2待收货 3已完成 4已取消',
    `receiver_name` VARCHAR(50) NOT NULL,
    `receiver_phone` VARCHAR(20) NOT NULL,
    `address` VARCHAR(300) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `pay_time` DATETIME DEFAULT NULL,
    `ship_time` DATETIME DEFAULT NULL,
    `finish_time` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `product_name` VARCHAR(200) NOT NULL,
    `product_image` VARCHAR(500) NOT NULL DEFAULT '',
    `price` DECIMAL(10,2) NOT NULL,
    `quantity` INT NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单明细表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT DEFAULT NULL,
    `username` VARCHAR(50) DEFAULT '',
    `module` VARCHAR(50) NOT NULL COMMENT '模块',
    `action` VARCHAR(50) NOT NULL COMMENT '操作',
    `detail` VARCHAR(500) DEFAULT '' COMMENT '详情',
    `ip` VARCHAR(50) DEFAULT '',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';

-- ===================== 初始数据 =====================
-- 管理员账号: admin / 123456
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `nickname`, `role`, `status`) VALUES
(1, 'admin', '$2a$10$W8YIBLH9yqtHfebcfW2I6.bXxcO9R2TUl0UT/EXgtu5iCHq2dNwye', '管理员', 1, 1);

-- 普通用户账号: user / 123456
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `nickname`, `role`, `status`) VALUES
(2, 'user', '$2a$10$W8YIBLH9yqtHfebcfW2I6.bXxcO9R2TUl0UT/EXgtu5iCHq2dNwye', '普通用户', 0, 1);

INSERT IGNORE INTO `category` (`id`, `name`, `icon`, `sort_order`) VALUES
(1, '手机', '/img/cat-phone.svg', 1),
(2, '平板', '/img/cat-pad.svg', 2),
(3, '耳机', '/img/cat-audio.svg', 3),
(4, '充电器', '/img/cat-charger.svg', 4),
(5, '手机壳', '/img/cat-case.svg', 5),
(6, '数据线', '/img/cat-cable.svg', 6);

INSERT IGNORE INTO `product` (`id`, `name`, `price`, `original_price`, `main_image`, `images`, `description`, `category_id`, `stock`, `sales`, `status`) VALUES
(1, 'iPhone 15 Pro Max 256GB 原色钛金属', 9999.00, 10999.00,
 '/img/iphone15pro.webp',
 '["/img/iphone15pro.webp"]',
 '<p>A17 Pro芯片，钛金属设计，4800万像素主摄，超视网膜XDR显示屏</p>', 1, 500, 1200, 1),

(2, '华为 Mate 60 Pro 12GB+512GB 雅丹黑', 6999.00, 7999.00,
 '/img/huawei-mate60.jpeg',
 '["/img/huawei-mate60.jpeg"]',
 '<p>麒麟9000S芯片，卫星通话，超可靠玄武架构，XMAGE影像</p>', 1, 300, 2500, 1),

(3, '小米14 Ultra 16GB+512GB 黑色', 5999.00, 6499.00,
 '/img/xiaomi14.webp',
 '["/img/xiaomi14.webp"]',
 '<p>骁龙8 Gen3，徕卡光学Summilux镜头，大师人像</p>', 1, 800, 900, 1),

(4, 'Samsung Galaxy S24 Ultra 12GB+256GB', 9699.00, 10199.00,
 '/img/samsung-s24.webp',
 '["/img/samsung-s24.webp"]',
 '<p>Galaxy AI智能，钛金属框架，2亿像素，S Pen</p>', 1, 400, 600, 1),

(5, 'iPad Pro M4 11英寸 256GB', 8999.00, 9499.00,
 '/img/ipad-pro.webp',
 '["/img/ipad-pro.webp"]',
 '<p>M4芯片，超薄设计，Tandem OLED，ProMotion自适应刷新率</p>', 2, 200, 350, 1),

(6, 'AirPods Pro 2 USB-C', 1799.00, 1999.00,
 '/img/airpods-pro.webp',
 '["/img/airpods-pro.webp"]',
 '<p>自适应音频，个性化空间音频，USB-C充电，IP54防水</p>', 3, 1000, 3000, 1),

(7, '华为 MatePad Pro 13.2英寸', 5199.00, 5699.00,
 '/img/huawei-matepad.webp',
 '["/img/huawei-matepad.webp"]',
 '<p>星闪连接，柔性OLED屏，天生会画，PC级WPS</p>', 2, 150, 200, 1),

(8, '索尼 WH-1000XM5 头戴式降噪耳机', 2299.00, 2999.00,
 '/img/sony-xm5.webp',
 '["/img/sony-xm5.webp"]',
 '<p>行业领先降噪，30小时续航，多点连接，LDAC高解析度音频</p>', 3, 600, 800, 1);

INSERT IGNORE INTO `banner` (`id`, `image_url`, `product_id`, `sort_order`) VALUES
(1, '/img/iphone15pro.webp', 1, 1),
(2, '/img/huawei-mate60.jpeg', 2, 2),
(3, '/img/xiaomi14.webp', 3, 3);
