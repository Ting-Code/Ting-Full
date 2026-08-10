# Ting-Full：国内通用全栈骨架

> 目标：先熟悉**最常见架构**，再由浅入深抠框架细节。  
> 技术栈：`Java + Spring Cloud Alibaba + MySQL + Redis + MyBatis-Plus + Flyway + React + Ant Design`。

## 骨架已包含

| 能力 | 状态 |
|------|------|
| 多模块：gateway / user / biz / common | ✅ |
| Nacos 注册发现 | ✅ |
| Gateway 路由 + 跨域 + Token 鉴权 | ✅ |
| OpenFeign 服务调用 | ✅ |
| MySQL + MyBatis-Plus + Flyway | ✅ |
| Redis（登录 Token、商品列表缓存） | ✅ |
| 统一返回体 / 全局异常 | ✅ |
| springdoc（Apifox 可导入） | ✅ |
| 本地一键起 Redis+Nacos 脚本 | ✅ |
| 前端 React + Ant Design（`ting-web`） | ✅ |
| RBAC | ⏳ 未做 |

## 架构

```text
ting-web (React + Ant Design) :5173
      │  proxy /api
      ▼
 ting-gateway:8080
      ├─ /api/user/**  → ting-user:8081
      └─ /api/biz/**   → ting-biz:8082
              ↓
         Nacos + MySQL + Redis
```

## 快速启动

### 1. MySQL（一次）

执行 `sql/init.sql` 建空库 `ting`。默认 `root / 123456`。  
表由服务启动时 Flyway 自动创建。

### 2. Redis + Nacos

```bash
cp scripts/infra.env.example scripts/infra.local.env
bash scripts/start-infra.sh
```

### 3. 后端

```bash
mvn clean install -DskipTests
mvn -pl ting-user spring-boot:run
mvn -pl ting-biz spring-boot:run
mvn -pl ting-gateway spring-boot:run
```

### 4. 前端（需 Node 20+，推荐 22）

```bash
# Windows nvm 示例
nvm use 22.14.0

cd ting-web
npm install
npm run dev
```

打开 http://127.0.0.1:5173 ，默认账号 `admin / 123456`。

### 5. Apifox

| 接口 | 说明 |
|------|------|
| `POST /api/user/login` | 白名单 |
| 其它 `/api/**` | Header `X-Token` |

## 端口

| 服务 | 端口 |
|------|------|
| ting-web | 5173 |
| gateway | 8080 |
| user | 8081 |
| biz | 8082 |
| nacos | 8848 |
| mysql | 3306 |
| redis | 6379 |
