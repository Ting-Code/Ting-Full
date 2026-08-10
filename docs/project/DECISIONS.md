# 决策记录

按时间追加；已落地的决策不要偷偷改结论，新结论另开一条。

## 2026-08：后端主栈选 Spring Cloud Alibaba

- **原因：** 国内全栈/后端岗位量最大的组合是 Java + Spring + MySQL + Redis。
- **做法：** 单仓多模块，先熟悉通用架构再深挖组件。

## 2026-08：本地中间件不用 Docker 做默认方案

- **原因：** 本机已有 MySQL；Docker Desktop 更吃资源；协作用脚本更可控。
- **做法：** `scripts/start-infra.sh` 起 Redis + Nacos；MySQL 各人本机实例。

## 2026-08：库表用 Flyway，不用手改库同步

- **原因：** 多人协作与上线需要可回放的结构变更。
- **做法：** 各服务 `db/migration`；历史表 `flyway_history_user` / `flyway_history_biz`。

## 2026-08：鉴权放在网关查 Redis Token

- **原因：** 统一入口校验，业务服务可信任 `X-User-Id`（网关会剥离客户端伪造头）。
- **做法：** 登录写 `login:token:{token}`；白名单 `/api/user/login`。

## 2026-08：前端用 React + Ant Design，不用 Vue

- **原因：** 用户明确要求；与全球/互联网前端主流一致；Antd 适配管后台。
- **做法：** Vite + React 19 + Antd 6。

## 2026-08：前端 pnpm workspace，目录 `ting-web/`

- **原因：** monorepo 管理多包；与后端 `ting-*` 前缀统一。
- **做法：** `ting-web/apps/admin`（`@ting/admin`）、`ting-web/packages/shared`（`@ting/shared`）。

## 2026-08：README 与过程文档分离

- **原因：** README 对外展示；进度/决策给 AI 与协作者，避免 README 变成日记。
- **做法：** 过程写入 `docs/project/`；Cursor rule 要求 AI 优先阅读。

## 2026-08：RBAC 第一版（角色级）

- **原因：** 国内后台系统必备；承接网关鉴权继续学权限模型。
- **做法：** `sys_role` / `sys_user_role`；登录把 roles 写入 Redis；网关对商品 POST/PUT/DELETE 校验 ADMIN；演示账号 `admin`(ADMIN)、`user`(USER)。
