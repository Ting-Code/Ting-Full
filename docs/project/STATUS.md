# 当前状态

> 更新日期：2026-08-10

## 已完成

- [x] Maven 多模块：`ting-gateway` / `ting-user` / `ting-biz` / `ting-common`
- [x] Nacos 注册发现；Gateway 路由与跨域
- [x] 网关 Redis Token 鉴权（`X-Token` → `X-User-Id`）
- [x] OpenFeign 示例（biz → user）
- [x] MySQL + MyBatis-Plus + Flyway（分服务历史表）
- [x] Redis：登录 Token、商品列表缓存
- [x] springdoc OpenAPI
- [x] 本地 `scripts/start-infra.sh`（Redis + Nacos；MySQL 用本机）
- [x] 前端 `ting-web` pnpm workspace：`@ting/admin` + `@ting/shared`
- [x] 管理端登录 + 商品列表联调
- [x] 文档拆分：对外 README + 本目录过程记录
- [x] RBAC：角色表 + 登录带 roles；网关对商品写接口要求 ADMIN；前端按角色显示操作

## 进行中 / 待办

- [ ] 更细粒度权限（菜单/按钮权限码，不止角色）
- [ ] 前端：商品编辑、统一错误体验打磨
- [ ] CI（后端测试 / 前端 build）
- [ ] 将本地未推送的提交推到 GitHub（网络/代理恢复后）

## 已知问题 / 注意

- 本机 MySQL 密码可能是 `123456`（以 `application.yml` 为准），与早期 `root123` 文档示例不同。
- Windows 下 GitHub 推送可能需代理（如 `socks5://127.0.0.1:7890`）。
- 前端需 Node ≥ 20；可用 nvm 切换到 22.x 后在 `ting-web` 下执行 `pnpm install`。
- 共用库 `ting` 时 Flyway 使用 `baseline-version: 0` + 分历史表，避免后启动服务跳过 V1。
