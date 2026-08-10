# 项目上下文（给 AI）

> 人看 [LEARNING.md](./LEARNING.md)。本文件：带学规则、工程状态、约定。

## 当前阶段

用户目标：**国内通用 Java 全栈高频技能**（Boot3 + SCA + 联调要点）。  
课程：**C01→C30**（见 LEARNING.md）。**当前课：C01**。  
本仓库 = 可选对照；讲课以通用方案为主。

### 带学规则（必须遵守）

1. 每课：① 知识点浅→深 → ② 通用写法（可选对照仓库）→ ③ 是/否或填空复盘。  
2. 零 Java 基础；可用前端对照；不用生活比喻；题难就直接给答案。  
3. **高频实战优先**；遇到 JSP/Servlet 手写/SSH/反射深挖/Security 全家桶等 → 按 LEARNING「不学清单」劝退。  
4. 未点名新功能时不要堆业务需求；改代码服务于当前课练习。  
5. 上完一课：勾选 LEARNING，更新本文件「当前课」。

## 工程进度（仓库）

**已完成：** 多模块 gateway/user/biz/common；Nacos；Gateway；Redis Token；RBAC；Feign；MP+Flyway；缓存；springdoc；前端登录+商品。  
**学习曾改动：** `/user/ping`+白名单；`/user/search` 昵称模糊。  
**功能待办（技能课后再说）：** 细权限；商品编辑；CI。

## 约定

- 模块 `ting-*`；鉴权 `X-Token`；联调网关 `8080`  
- 文档仅 LEARNING + CONTEXT；根 README 对外  

## 决策记录

| 何时 | 决策 | 做法 |
|------|------|------|
| 2026-08 | 技术栈对齐国内岗位 | Java + SCA + MySQL + Redis + React |
| 2026-08 | 补全 Boot3 + SCA 课 | C08–C17 Boot3（含双码/DTO/OpenAPI）；C22–C29 SCA（含 CORS） |
| 2026-08 | 补联调高频 | C11 HTTP vs 业务码；C13 DTO；C17 Apifox；C26 CORS |
| 2026-08 | 不学过时/边角 | JSP/SSH/双 ORM/Security 全配等见 LEARNING |
| 2026-08 | 每课三段式 | 知识点→实战→复盘 |
| 2026-08 | README / 过程分离 | 人 LEARNING；AI CONTEXT |
