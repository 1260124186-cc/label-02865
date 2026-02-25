# PhoneMall 手机商城

京东风格的手机商城全栈项目

## How to Run

```bash
# 克隆项目后，在根目录执行
docker-compose up --build -d
```

等待所有服务启动完成（首次构建约 3-5 分钟），即可访问：

- 手机商城前端：http://localhost:8081
- 后端 API：http://localhost:8080

如需停止：

```bash
docker-compose down
```

如需清除数据重新初始化：

```bash
docker-compose down -v
docker-compose up --build -d
```

## Services

| 服务 | 容器名 | 端口映射 | 说明 |
|------|--------|----------|------|
| MySQL 8.0 | phonemall-mysql | 3306:3306 | 数据库，自动执行建表和初始数据 |
| Spring Boot 后端 | phonemall-backend | 8080:8080 | REST API 服务 |
| 前端 (Nginx) | phonemall-frontend | 8081:80 | Vue 3 手机商城 |

## 测试账号

| 用户名 | 密码 |
|--------|------|
| user | 123456 |

用户可通过注册页面自行注册。

## 题目内容

基于vue.js生成类似于京东的手机商城

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios + SCSS |
| 后端 | Java 17 + Spring Boot 3.2 + MyBatis-Plus + Spring Security |
| 数据库 | MySQL 8.0 |
| 认证 | JWT (jjwt 0.12.5) |
| 部署 | Docker + Docker Compose + Nginx |

## 项目结构

```
├── docker-compose.yml          # Docker 编排
├── .gitignore
├── db/init/                    # MySQL 初始化脚本
│   └── 01-schema.sql
├── docs/
│   └── project_design.md       # 系统设计文档(架构图/ER图/接口清单)
├── backend/                    # Spring Boot 后端
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/phonemall/
│       │   ├── config/         # JWT/Security/CORS/全局异常/MP配置
│       │   ├── interceptor/    # JWT 拦截器
│       │   ├── aspect/         # AOP 操作日志
│       │   ├── common/         # Result/PageResult/BusinessException
│       │   ├── entity/         # 9个实体类
│       │   ├── mapper/         # MyBatis-Plus Mapper
│       │   ├── service/        # Service 接口 + impl
│       │   ├── controller/     # 7个 Controller
│       │   └── dto/            # DTO
│       └── resources/
│           ├── application.yml
│           └── schema.sql
└── frontend-admin/             # Vue 3 前端（手机商城）
    ├── Dockerfile
    ├── nginx.conf
    ├── package.json
    └── src/
        ├── api/                # API 请求层
        ├── store/              # Pinia 状态管理
        ├── router/             # 路由
        ├── utils/              # Axios 封装
        ├── assets/styles/      # 全局 SCSS
        └── views/
            ├── home/           # 首页 + 搜索
            ├── category/       # 分类
            ├── product/        # 商品详情
            ├── cart/           # 购物车
            ├── user/           # 个人中心/登录/订单
            └── order/          # 订单/结算/地址
```

## 功能清单

### 用户端 (Mobile H5) - 访问 http://localhost:8081
- 首页：轮播图 + 分类导航 + 热销排行 + 推荐商品
- 分类：左侧分类栏 + 右侧商品列表
- 搜索：关键词搜索商品
- 商品详情：图片轮播 + 价格 + 描述 + 加购/购买
- 购物车：增删改查 + 全选 + 结算
- 订单：创建订单 + 订单列表 + 取消/确认收货
- 个人中心 + 登录/注册

### 工程特性
- JWT 认证 + 拦截器鉴权
- 全局异常处理 (GlobalExceptionHandler)
- AOP 操作日志自动记录
- 参数校验 (@Valid + DTO)
- BCrypt 密码加密
- CORS 跨域配置
- MyBatis-Plus 分页插件
- 统一响应格式 (Result<T>)
