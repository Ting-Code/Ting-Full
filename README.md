# Ting-Full

基于国内主流技术栈的全栈示例项目：Spring Cloud Alibaba 微服务后端 + React 管理端。

适合学习「网关鉴权、服务拆分、库表迁移、前后端联调」的完整链路。

## 技术栈

| 层 | 技术 |
|----|------|
| 前端 | React 19、Ant Design 6、Vite、pnpm workspace、TypeScript |
| 网关 | Spring Cloud Gateway、Redis Token 鉴权 |
| 服务 | Spring Boot 3、Spring Cloud Alibaba、OpenFeign、Nacos |
| 数据 | MySQL 8、MyBatis-Plus、Flyway、Redis |
| 文档 | springdoc OpenAPI（可导入 Apifox） |

## 架构

```text
ting-web/apps/admin  ──/api──►  ting-gateway
                                    ├─► ting-user
                                    └─► ting-biz
                                         │
                              Nacos · MySQL · Redis
```

## 仓库结构

```text
ting-web/          前端 pnpm monorepo（admin + shared）
ting-gateway/      API 网关
ting-user/         用户与登录
ting-biz/          商品等业务
ting-common/       公共返回体、常量
scripts/           本地 Redis / Nacos 启动脚本
sql/               建库脚本
docs/project/      项目过程记录（给协作者 / AI 用）
```

## 快速开始

**环境：** JDK 21、Maven 3.9+、Node 20+（推荐 22）、本机 MySQL / Redis，以及 Nacos（可用脚本拉起）。

```bash
# 1. 建库
# 执行 sql/init.sql（库名 ting；默认示例账号见下方）

# 2. 中间件
cp scripts/infra.env.example scripts/infra.local.env
bash scripts/start-infra.sh

# 3. 后端
mvn clean install -DskipTests
mvn -pl ting-user spring-boot:run
mvn -pl ting-biz spring-boot:run
mvn -pl ting-gateway spring-boot:run

# 4. 前端
cd ting-web
pnpm install
pnpm dev
```

- 管理端：http://127.0.0.1:5173  
- 演示账号：`admin / 123456`（ADMIN，可改商品）、`user / 123456`（USER，只读）  
- 网关：http://127.0.0.1:8080  

登录后请求需携带请求头 `X-Token`（前端已自动处理）。OpenAPI：`http://127.0.0.1:8081/v3/api-docs`、`http://127.0.0.1:8082/v3/api-docs`。

## 端口

| 服务 | 端口 |
|------|------|
| 管理端 | 5173 |
| 网关 | 8080 |
| 用户服务 | 8081 |
| 业务服务 | 8082 |
| Nacos | 8848 |
| MySQL | 3306 |
| Redis | 6379 |

## 说明

- 本地中间件默认不依赖 Docker；配置见 `scripts/`。  
- 库表变更使用各服务内 Flyway 脚本（`db/migration`）。  
- 项目演进与决策记录见 [`docs/project/`](docs/project/)。
