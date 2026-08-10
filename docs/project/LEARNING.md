# 全栈学习流程（国内通用 · 高频优先）

> **目标：** 具备国内常见 Java 全栈交付能力（库表 → 接口 → 鉴权 → 联调），不是把本仓库功能做完。  
> **本仓库：** 可选对照/练手；课内以「通用做法」为主，有示例时再指到 Ting-Full。  
> **每课：** ① 知识点浅→深 → ② 通用写法/示例 → ③ 极简复盘（是/否、填空）。  
> **原则：** 只学高频实战；过时与边角进「不学清单」。零 Java 基础；可用前端对照。

进度把 `[ ]` 改成 `[x]`。当前从 **C01** 开始。

---

## 能力地图（你要练成什么）

```text
Java 够用 → SQL → Spring Boot 3（含 R/双码/DTO/文档）
  → MP + Flyway → Redis 登录
  → SCA（Nacos / Gateway / CORS / Feign）
  → 联调验收
```

岗位高频栈：`Java 17/21` · **`Spring Boot 3`** · **`Spring Cloud Alibaba`** · `MySQL` · `Redis` · `MyBatis-Plus` · 管理端 React/Vue+Antd。

---

## 总览

| 阶段 | 课 | 主题 | 状态 |
|------|----|------|------|
| **J Java** | C01～C04 | 类方法、String、List/Map、异常 | [ ] |
| **M 工程** | C05 | Maven 多模块 | [ ] |
| **Q MySQL** | C06～C07 | SQL、索引事务 | [ ] |
| **B Spring Boot 3** | C08 | Boot3、starter、jakarta | [ ] |
| | C09 | 启动、yml、分层 | [ ] |
| | C10 | REST + 统一返回 R | [ ] |
| | C11 | HTTP 状态码 vs 业务 code | [ ] |
| | C12 | 依赖注入 | [ ] |
| | C13 | DTO / 不直接暴露实体 | [ ] |
| | C14 | 校验 + 全局异常 | [ ] |
| | C15 | `@Transactional` | [ ] |
| | C16 | 多环境配置 | [ ] |
| | C17 | OpenAPI / Apifox | [ ] |
| **D 数据** | C18～C19 | MyBatis-Plus、Flyway | [ ] |
| **R 登录** | C20～C21 | Redis Token、BCrypt | [ ] |
| **A SCA** | C22 | SCA 全景与为何拆服务 | [ ] |
| | C23 | Nacos 注册发现 | [ ] |
| | C24 | Nacos 配置中心（浅） | [ ] |
| | C25 | Gateway 路由 | [ ] |
| | C26 | CORS 跨域 | [ ] |
| | C27 | 网关统一鉴权 | [ ] |
| | C28 | OpenFeign | [ ] |
| | C29 | Sentinel（概念） | [ ] |
| **F 验收** | C30 | 联调排错 + 小接口验收 | [ ] |

C01～C07 细则见「J/M/Q」；**C11 / C13 / C17 / C26** 为补全的联调高频点。

---

## 不学清单（节约时间）

| 不学 | 原因 |
|------|------|
| JSP / Servlet 手写 / SSH | 过时 |
| Hibernate/JPA 与 MP 双线 | 先精 MP |
| Spring Security 全套死磕 | 先 Token + 网关/过滤器 + RBAC |
| Boot 自动配置源码精读 | 先会用 starter |
| 自写 Starter（可后置） | 非入门必需 |
| MapStruct 复杂映射专班 | C13 先手写/简单拷贝即可 |
| Seata / 分布式事务落地 | 知「尽量避免」即可 |
| Sentinel 复杂规则平台深配 | C29 只保留概念 |
| Dubbo 与 Feign 双线 | 先精 OpenFeign |
| K8s / Service Mesh | 后置 |
| 反射、JUC 大部、JVM 调优专班 | 后置 |

---

## J. Java 够用

### C01 类、方法、包

**①** 类 ≈ 模块/对象模板；方法 ≈ function；`package` ≈ 命名空间。`public` 先混眼熟。  
选读：廖雪峰 基本结构、包、方法。  
**②** 任意 `*Controller.java` / `*Service.java` 看 `package` 与 `class`、方法。  
**③** Controller 是不是一个 class？是/否  

### C02 String 与常用类型

**①** `String` 文本；`Long`/`Integer` 数字；比字符串内容用 `.equals` 不用 `==`。钱用 `BigDecimal`（综合课再见）。  
选读：字符串。  
**②** 看登录请求 DTO 字段类型。  
**③** 用户名常用类型？______（String）  

### C03 List / Map

**①** 多个对象 → `List<T>` ≈ 数组；按 id 查找 → `Map<K,V>` ≈ 前端 Map/对象字典。业务里这两样最高频。Set 去重用到再学。  
选读：[集合 List](https://liaoxuefeng.com/books/java/collection/list/index.html)、[Map](https://liaoxuefeng.com/books/java/collection/map/index.html)。  
**②** 看返回列表的接口（如 search）类型 `List<...>`。  
**③** 返回很多行数据，用 List 合适吗？是/否  

### C04 异常

**①** `throw` ≈ 前端 throw Error；业务失败常用自定义 `BizException`（或项目里同类名）。  
选读：异常简介。  
**②** 看 Service 里 `throw new BizException(...)`。  
**③** 本类项目密码错误，常见是 throw 还是只靠 return？______（throw）  

---

## M. 工程

### C05 Maven

**①** `pom.xml` 管依赖与版本；多模块 = 多个子项目共用父 POM。命令：`mvn package` / 指定模块运行。  
选读：[Maven 介绍](https://liaoxuefeng.com/books/java/maven/basic/index.html)。  
**②** 看任意项目根 `pom.xml` 的 `modules` / `dependencies`。  
**③** 第三方库主要靠 pom 声明吗？是/否  

---

## Q. MySQL

### C06 建表与 CRUD

**①** 主键、非空、唯一、注释；`SELECT/INSERT/UPDATE/DELETE`；`WHERE` + `ORDER BY` + `LIMIT`。  
**②** 看任意 `V1__*.sql` 或业务表结构。  
**③** 查多行要不要写 SELECT？是/否  

### C07 索引与事务（够用）

**①** 索引加快 WHERE/JOIN，别乱加；事务 = 多步同成同败，对应 `@Transactional`。隔离级别名字能听懂即可。  
**②** 看唯一索引、或 Service 上事务注解（有则看，无则记概念）。  
**③** 改表结构推荐版本化 SQL（Flyway/Liquibase）吗？是/否  

---

## B. Spring Boot 3（国内 Web 主力）

> Boot 3 主流；体感差异：`javax.*` → **`jakarta.*`**，JDK **17+**。

### C08 Boot3 是什么

**① 浅：** Boot = 快速做 Web：加 `starter` 就能用常用能力。  
**深一点：** `starter-web` / `validation` / `data-redis`；版本常由父 POM 管理；校验用 `jakarta.validation`。  
**②** 看父 POM 与 `starter-*`。  
**③** Boot3 校验是否更常见在 `jakarta.validation`？是/否  

### C09 启动、yml、分层

**①** `@SpringBootApplication`；yml 配端口/数据源/Redis；Controller → Service → Mapper。  
**②** 看启动类 + yml + 分层目录。  
**③** 业务逻辑更常放 Service 吗？是/否  

### C10 REST + 统一返回 R

**①** `@GetMapping`/`@PostMapping`、`@RequestBody`、`@PathVariable`、`@RequestParam`。  
国内通用：`{ code, message, data }`（`R`/`Result`）。成功 code 常见 `0` 或 `200`。  
**②** 看统一返回类 + 任一接口。  
**③** 统一返回是为了前端好解析吗？是/否  

### C11 HTTP 状态码 vs 业务 code

**① 浅：** 浏览器/网关先看 **HTTP 状态**（200/400/401/403/500）；body 里还有业务 **`code`**。  
**深一点（国内常见）：**  
- 很多项目 HTTP 也返回 200，用 body 的 `code≠0` 表示失败（前端 axios 要看 `data.code`）  
- 也有项目：校验失败 HTTP 400、未登录 401，同时 body 带 `R`  
- 联调时两边都要看，不要只看一个  

**②** 故意调一个失败接口，对比「HTTP 状态」和响应 JSON 里的 `code`。  
**③** 只看 HTTP=200，是否一定表示业务成功？是/否  

### C12 依赖注入

**①** 不要 `new Service()`；`@Service`/`@RestController` 注册；构造器注入（`final` + `@RequiredArgsConstructor`）。  
**②** 看 Controller/Service 注入字段。  
**③** 推荐业务里自己 new Mapper 吗？是/否  

### C13 DTO / 不直接暴露实体

**① 浅：** **DTO/VO** = 给接口用的数据形状；**Entity** = 对应数据库表。  
**深一点：**  
- 请求用 `LoginRequest`，响应用 `LoginResponse` / `UserProfile`，避免把带 `password` 的实体直接返回  
- 可用 `@JsonIgnore` 挡字段，但正规做法是「接口层用 DTO」  
- 对照前端：请求/响应 TS 类型，不等于数据库一行的全部字段  

**②** 对比 `SysUser`（实体）与 `LoginRequest`/`UserProfile`（DTO）；看密码字段如何被忽略。  
**③** 登录接口更适合直接返回数据库实体（含密码哈希）吗？是/否  

### C14 校验 + 全局异常

**①** `@NotBlank` + `@Valid`；`@RestControllerAdvice` + `@ExceptionHandler`；校验失败与业务异常分方法。  
**②** 看 DTO、Controller、GlobalExceptionHandler。  
**③** 没有 `@Valid` 时字段规则会自动跑吗？会/不会  

### C15 声明式事务

**①** 多步写库加 `@Transactional`；只读常不加；异常被吞可能不回滚。  
**②** 看多表写入的 Service（有则看）。  
**③** 事务注解更常加在 Service 吗？是/否  

### C16 多环境配置

**①** `application-dev.yml` / `prod`；`spring.profiles.active`；密钥不进仓库。  
**②** 看项目如何区分环境。  
**③** 生产密码适合写死在代码里吗？是/否  

### C17 OpenAPI / Apifox

**① 浅：** 用文档描述有哪些接口；**Apifox/Postman** 导入后可调通。  
**深一点：** Spring Boot 常用 **springdoc-openapi**（Boot3），浏览器打开 swagger-ui 或导出 `/v3/api-docs` 给 Apifox。联调前先有文档，少口口相传。  
**②** 若项目有 springdoc：打开 `/swagger-ui.html` 或导入 api-docs；无则记「要接文档工具」。  
**③** 前后端联调时，有接口文档会更省事吗？是/否  

---

## D. 数据访问

### C18 MyBatis-Plus

**①** `BaseMapper`；`LambdaQueryWrapper` 的 `eq`/`like`；`@TableName`。  
**②** 看 Mapper + Service 查询。  
**③** MP 能减少大量手写 SQL 吗？是/否  

### C19 Flyway

**①** 版本化改表；只新增 `Vn__xxx.sql`；禁止改已执行旧脚本。  
**②** 看 `db/migration`。  
**③** 能不能改已经执行过的 V1？能/不能  

---

## R. 登录态

### C20 Redis + Token

**①** 登录发 Token；Redis 存 token→用户/角色；请求头携带；多实例共享。  
**②** 看 login 写 Redis、过滤器校验。  
**③** Token 放 Redis 有利于多机共享吗？是/否  

### C21 BCrypt

**①** 禁止明文；BCrypt；`matches(明文, 密文)`。  
**②** 看 PasswordEncoder。  
**③** 库里应存明文密码吗？是/否  

---

## A. Spring Cloud Alibaba

> 国内常见：**Nacos + Gateway + Feign（+ Sentinel 概念）**。

### C22 SCA 全景与为何拆服务

**①** 拆服务有边界也有代价。认名字：Nacos、Gateway、Feign、Sentinel（概念）、Seata（知避免）。  
**②** 对照多模块：谁是网关、有哪些服务名。  
**③** 浏览器是否通常只打网关？是/否  

### C23 Nacos 注册发现

**①** `spring.application.name` + Nacos 地址；按服务名找实例。  
**②** 看 discovery 配置。  
**③** 注册的是实例（含地址端口）吗？是/否  

### C24 Nacos 配置中心（浅）

**①** 配置可放 Nacos 共享；很多项目仍用本地 yml——先懂「能集中管」。  
**②** 区分本地 yml vs Nacos config。  
**③** 必须把所有配置放 Nacos 才算微服务吗？是/否  

### C25 Gateway 路由

**①** `Path` + `uri: lb://服务名` + `StripPrefix`。  
**②** 看 gateway 路由 yml。  
**③** `lb://服务名` 是按服务名转发吗？是/否  

### C26 CORS 跨域

**① 浅：** 浏览器前端域名/端口与接口不一致时会跨域；服务端（常在**网关**）要配 CORS。  
**深一点：** `Access-Control-Allow-Origin` 等；预检 OPTIONS；凭证/Cookie 时不能随意 `*`。前后端分离管理端几乎必碰。  
**②** 看 gateway 的 `globalcors`（或后端 CORS 配置）。  
**③** 管理端域名和网关端口不同，是否经常需要配 CORS？是/否  

### C27 网关统一鉴权

**①** Filter 校验 Token；白名单放行登录；写入用户头给下游；勿信客户端伪造头。  
**②** 看鉴权 Filter。  
**③** 登录接口通常应在白名单吗？是/否  

### C28 OpenFeign

**①** `@FeignClient` 服务间调用；注意超时与失败。  
**②** 看 Feign 接口。  
**③** Feign 主要用于服务调服务吗？是/否  

### C29 Sentinel（概念）

**①** 限流、熔断；面试常问概念；复杂规则后置。  
**②** 口述即可。  
**③** 下游挂了还疯狂重试，容易更糟吗？是/否  

---

## F. 联调与验收

### C30 联调 + 小接口验收

**①** 契约（含 DTO 字段）、HTTP 与业务 code、鉴权头、CORS；排错看状态/body/日志/SQL/Redis；会用 Apifox。  
验收：带校验接口 + 统一返回 + 需登录（经网关）+ 能讲清链路。  
**②** 走通：登录 → Token → 业务；可选落地一小接口。  
**③** 联调是否应优先走网关？是/否  

---

## 推荐外部资料（少而精）

- Java / SQL：廖雪峰高频章  
- Spring Boot 3：官方 Getting Started（Web、Validation）  
- springdoc / Apifox：会导入 `/v3/api-docs` 即可  
- SCA：Nacos / Gateway / OpenFeign 入门文档（不做交易引擎长篇）  

---

## 给 AI 的带学指令

1. 目标是国内通用高频技能（Boot3 + SCA + 联调要点），不是堆仓库功能。  
2. 每课 ①→②→③；② 先通用，再可选对照仓库。  
3. 补全课重点：C11 双码、C13 DTO、C17 文档、C26 CORS。  
4. 跳过「不学清单」；复盘极简；答不出直接给答案。  
5. 更新勾选与 `CONTEXT.md` 当前课号（课程为 **C01～C30**）。
