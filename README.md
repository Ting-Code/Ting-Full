# Ting-Full：国内通用全栈骨架

> 目标：先熟悉**最常见架构**，再由浅入深抠框架细节。  
> 技术栈：`Java + Spring Cloud Alibaba + MySQL + Redis + MyBatis-Plus + Flyway`（Vue 后续再加）。

## 骨架已包含

| 能力 | 状态 |
|------|------|
| 多模块：gateway / user / biz / common | ✅ |
| Nacos 注册发现 | ✅ |
| Gateway 路由 + 跨域 | ✅ |
| OpenFeign 服务调用 | ✅ |
| MySQL + MyBatis-Plus + Flyway | ✅ |
| Redis（登录 Token、商品列表缓存） | ✅ |
| 统一返回体 / 全局异常 | ✅ |
| springdoc（Apifox 可导入） | ✅ |
| 本地一键起 Redis+Nacos 脚本 | ✅ |
| 前端 Vue | ⏳ 未做 |
| 网关鉴权、RBAC | ✅ 网关 Token（Redis）；RBAC 未做 |

## 架构

```text
Apifox / 浏览器
      │
      ▼
 ting-gateway:8080
      ├─ /api/user/**  → ting-user:8081
      └─ /api/biz/**   → ting-biz:8082
              ↓
         Nacos + MySQL + Redis
```

## 快速启动

### 1. MySQL（一次）

```bash
# 执行 sql/init.sql，只建空库 ting
# 默认账号 root / 123456（不同则改 ting-user、ting-biz 的 application.yml）
```

表由服务启动时 **Flyway** 自动创建：

- `ting-user/.../db/migration/` → 历史表 `flyway_history_user`
- `ting-biz/.../db/migration/` → 历史表 `flyway_history_biz`
- 改表只新增 `V2__xxx.sql`，不要改已提交的旧脚本

### 2. Redis + Nacos

```bash
cp scripts/infra.env.example scripts/infra.local.env   # 按需改 REDIS_HOME
bash scripts/start-infra.sh
```

- Nacos：http://127.0.0.1:8848/nacos （nacos/nacos）
- 首次自动下载到 `tools/nacos`（已 gitignore）

### 3. 业务服务

```bash
mvn clean install -DskipTests
mvn -pl ting-user spring-boot:run
mvn -pl ting-biz spring-boot:run
mvn -pl ting-gateway spring-boot:run
```

### 4. 验证 / Apifox

| 接口 | 说明 |
|------|------|
| `POST http://127.0.0.1:8080/api/user/login` | 白名单，无需 Token |
| `GET http://127.0.0.1:8080/api/biz/products` | 需请求头 `X-Token: <登录返回的 token>` |
| `GET http://127.0.0.1:8080/api/user/me` | 需 `X-Token`；网关会注入 `X-User-Id` |

Apifox：`baseUrl=http://127.0.0.1:8080`，登录后把 `data.token` 存为环境变量，全局 Header 加 `X-Token: {{token}}`。

## 端口

| 服务 | 端口 |
|------|------|
| gateway | 8080 |
| user | 8081 |
| biz | 8082 |
| nacos | 8848 |
| mysql | 3306 |
| redis | 6379 |

## 目录

```text
ting-gateway / ting-user / ting-biz / ting-common
scripts/          # start-infra / stop-infra
sql/init.sql      # 仅建库
```
