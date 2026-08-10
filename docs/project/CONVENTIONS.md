# 仓库约定

## 命名

- 顶级模块/目录统一 `ting-` 前缀：`ting-gateway`、`ting-user`、`ting-biz`、`ting-common`、`ting-web`。
- 前端 npm 包使用 scope：`@ting/admin`、`@ting/shared`。

## 文档

- 根 `README.md`：对外介绍、架构、快速开始。**禁止**维护长篇进度勾选清单。
- `docs/project/`：目标、进度、决策、约定；AI 改功能前应先读。
- 更新进度时改 `STATUS.md`；新选型追加 `DECISIONS.md`。

## 工具

- 后端：Maven，JDK 21。
- 前端：在 `ting-web/` 下使用 **pnpm**（不要用 npm 装依赖）。
- 接口调试：Apifox 可导入各服务 `/v3/api-docs`；联调走网关 `8080`。

## 鉴权

- 客户端请求头：`X-Token`。
- 网关校验通过后写入：`X-User-Id`（禁止信任客户端自带的该头）。
