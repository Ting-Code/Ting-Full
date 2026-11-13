# Spring Boot 基础笔记

> 边学边写；对照实战课与仓库。正文只写技术与练习。

## 第1章 工程搭建

### 1.1 Boot 工程 · 入口类 · starter ★★★★★

**是什么**

Spring Boot = 在 Spring 上做的「约定大于配置」脚手架：少写 XML、内嵌 Tomcat、用 **starter** 一键拉齐常用依赖与自动配置。  
前后端分离时，Boot 项目主要职责是**提供 HTTP 接口**（JSON），不负责返回整页 HTML。

**为什么**

以前搭 SSM：依赖版本、XML、Tomcat 部署，半天起步。  
Boot：官网/IDEA 选依赖 → 生成工程 → 入口类一跑就起服务。  
微服务/Cloud 也建立在 Boot 之上，先打好地基。

#### 怎么建

1. [start.spring.io](https://start.spring.io) 或 IDEA → New → Spring Initializr  
2. 选 Maven、Java 版本、打 jar  
3. 依赖至少加 **Spring Web**（`spring-boot-starter-web`）  
4. 下载解压 / IDEA 打开 → 跑入口类的 `main`

IDEA 侧建议：项目 UTF-8、JDK 指向本机、Maven 可用（常配国内镜像加速）。

#### 标准目录（先认这些）

```
project/
├── pom.xml                 # 依赖与父工程
├── src/main/java/...       # 业务代码
│   └── XxxApplication.java # 入口类
├── src/main/resources/
│   └── application.yml|properties
└── src/test/java/          # 测试
```

入口类所在包是**扫描根**：Controller 等业务包应放在它下面（或同级子包），否则组件扫不到。

#### 入口类

```java
@SpringBootApplication
public class WikiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WikiApplication.class, args);
    }
}
```

`@SpringBootApplication` ≈ 开自动配置 + 组件扫描 + 配置类。  
`main` → `SpringApplication.run` → 启动内嵌容器，默认端口常为 **8080**（可在配置里改）。

#### starter 是什么

| 概念 | 含义 |
|------|------|
| `spring-boot-starter-parent` | 父 POM：统一依赖版本，子模块常**不用写 version** |
| `spring-boot-starter-web` | Web 场景：Spring MVC + 内嵌 Tomcat + JSON 等 |
| 其它 starter | 如 data-redis、validation… 按场景勾选 |

逻辑：你声明「我要 Web」→ Boot 按约定自动装配好常用 Bean；需要改端口、改行为再写配置覆盖。

案例：没加 `starter-web` 也能启动，但没有 HTTP 服务；加上后才有 DispatcherServlet、能写 `@RestController`。

> 补：热部署（devtools）改完少重启；第一个 `/hello` 接口与 yml 自定义项见后续小节。仓库对照时看各模块的 `*Application` 与 `pom` 里 starter 列表。

### 1.1 练习 ★★★★★

1. Boot 相对传统 SSM 搭建，核心收益？  
2. `@SpringBootApplication` 大致做哪几件事？  
3. 业务类为何要放在入口类包或其子包下？  
4. `spring-boot-starter-web` 带来什么？  
5. 父 POM 对依赖 version 的常见作用？  

**答案**

1. 快速建工程、约定配置、内嵌容器、starter 管依赖。  
2. 自动配置、组件扫描、标识配置类（组合注解）。  
3. 默认只扫描入口类所在包及子包。  
4. Web/MVC + 内嵌 Tomcat 等，能对外提供 HTTP。  
5. 统一版本管理，子依赖常可省略 version。

### 1.2 配置文件 · 多环境 · 常用注解 ★★★★★

**是什么**

用 `application.yml` / `.properties` 改端口、数据源等；自定义项用 `@Value` 注入；  
dev / test / prod 用 **profile** 分文件；常用注解标明「这是组件 / 接口 / 注入点」。

**为什么**

硬编码端口和密钥难改、易泄密。配置外置 → 同一套代码换环境只换配置。

#### yml 与 properties

两者 Boot 都能认；层级用 yml 缩进更清晰，点号写法对应 properties：

```yaml
# application.yml
server:
  port: 8880

test:
  hello: hello
```

```properties
server.port=8880
test.hello=hello
```

常见加载位置（记常用即可）：

- `classpath:/application.yml`（`src/main/resources`）
- `classpath:/config/application.yml`（`resources/config`）

同一项多处都写时，以**优先级更高**的为准（自己改端口试一次就记住）。  
另有 `bootstrap.yml`：多在 Cloud/配置中心场景；纯 Boot 应用通常不读。

#### 自定义配置 + `@Value`

```yaml
test:
  hello: hello
```

```java
@Value("${test.hello:默认值}")
private String testHello;
```

逻辑：启动时按占位符取值；配了用配置，**没配用冒号后默认值**，避免配置中心漏配直接起不来。

#### 多环境（profile）

```text
application.yml          # 公共
application-dev.yml      # 开发
application-prod.yml     # 生产
```

```yaml
# application.yml
spring:
  profiles:
    active: dev          # 激活哪套
```

或启动参数：`--spring.profiles.active=prod`。

案例：dev 连本机库、开 debug 日志；prod 连线上库、关调试 —— **代码同一份**，只换 active。

#### 本阶段先认的常用注解

| 注解 | 用途 |
|------|------|
| `@SpringBootApplication` | 入口：自动配置 + 扫描 |
| `@RestController` | 接口类，返回 JSON（≈ `@Controller` + `@ResponseBody`） |
| `@RequestMapping` / `@GetMapping` | 路径映射 |
| `@Value` | 注入配置项 |
| `@Component` / `@Service` / `@Repository` | 声明 Bean（层不同，扫描同类） |
| `@Autowired` | 按类型注入依赖 |

> 补：复杂配置可用 `@ConfigurationProperties` 整组绑定；热部署见 devtools。第一个完整 REST 见下一小点。

### 1.2 练习 ★★★★★

1. yml 里 `server.port: 8880` 对应 properties 怎么写？  
2. `@Value("${a.b:x}")` 中 `:x` 什么意思？  
3. 如何让应用使用 `application-prod.yml`？  
4. `@RestController` 和普通 `@Controller` 对接口的差别（前后端分离场景）？  
5. 为什么自定义项建议写默认值？  

**答案**

1. `server.port=8880`。  
2. 找不到 `a.b` 时用默认值 `x`。  
3. `spring.profiles.active=prod` 或启动参数指定。  
4. `@RestController` 直接写响应体（JSON）；`@Controller` 偏返回页面名。  
5. 配置中心/环境漏配时仍能启动或有兜底。

## 第2章 接口入门

### 2.1 第一个 REST 接口 ★★★★★

**是什么**

在 Boot 里写一个 HTTP 接口：类上 `@RestController`，方法上 `@GetMapping("/hello")`，启动后用浏览器或 HTTP Client 访问，返回字符串/JSON。

**为什么**

前后端分离：后端只提供接口数据，不返回整页 HTML。第一个 Hello 用来确认「工程能对外提供服务」。

#### 分层落点（先记包）

入口类包下新建 `controller` 包，接口类放这里（保证被组件扫描到）：

```text
.../wiki/
├── WikiApplication.java
└── controller/
    └── TestController.java
```

#### 最小可运行代码

```java
@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
```

| 注解 | 作用 |
|------|------|
| `@RestController` | 声明接口类；返回值直接当响应体（JSON/字符串） |
| `@Controller` | 偏返回**页面名**；前后端分离基本不用 |
| `@GetMapping("/hello")` | 只接受 GET，路径 `/hello` |
| `@RequestMapping("/hello")` | 默认多种方法都能进（不限定时易混淆） |

完整 URL ≈ `http://主机:端口` + `/hello`（端口看 yml，如 8080 / 8880）。

#### REST 风格与四种 Mapping（先有印象）

| 习惯 | 注解 | 常见用途 |
|------|------|----------|
| GET | `@GetMapping` | 查 |
| POST | `@PostMapping` | 增 |
| PUT | `@PutMapping` | 改 |
| DELETE | `@DeleteMapping` | 删 |

案例：方法标了 `@GetMapping`，你用 POST 去调 → **HTTP 405**（Method Not Allowed）。开发常踩，记住「注解限定的方法和实际请求不一致」。

路径风格对比（后面再写路径变量）：

- 传统：`/user?id=1`  
- REST 常见：`/user/1`（配合 `@PathVariable`，下节起会用到）

#### 怎么测

1. 启动入口类  
2. 浏览器访问 GET（只能测 GET）  
3. 或 IDEA **HTTP Client**：建 `xxx.http`

```http
GET http://localhost:8880/hello
```

> 补：POST/带 Body 用 HTTP Client 或 Apifox，比浏览器方便。Controller → Service → Mapper 完整分层见下一小点。

### 2.1 练习 ★★★★★

1. 为何用 `@RestController` 而不是 `@Controller`？  
2. 接口类包应放在哪？  
3. `@GetMapping` 的接口用 POST 访问会怎样？  
4. 写出返回 `"ok"`、路径 `/ping` 的最小方法。  
5. 完整访问地址由哪两部分拼成？  

**答案**

1. 直接写响应体（JSON/字符串）；`@Controller` 偏页面。  
2. 入口类所在包或其子包（如 `controller`）。  
3. 通常 405 Method Not Allowed。  
4. `@GetMapping("/ping") public String ping(){ return "ok"; }`（类上要有 `@RestController`）。  
5. `http://主机:端口` + 映射路径。

## 第3章 分层架构

### 3.1 Controller · Service · Mapper ★★★★★

**是什么**

后端按职责拆成三层（常见叫法）：

| 层 | 常见包名 | 干什么 |
|----|----------|--------|
| 表现 / 接口 | `controller` | 接 HTTP：解析参数、调 Service、返回结果 |
| 业务 | `service` | 业务规则、编排、事务边界常在这 |
| 持久 | `mapper`（或 dao） | 只跟数据库打交道（SQL / MyBatis） |

另有 **实体 entity/domain**：和表字段对应的 Java 对象，三层都会用到。

**为什么**

全写在 Controller 里：难测、难复用、改库要动接口。  
分层后：接口变了只改 Controller；换库/SQL 主要动 Mapper；业务规则集中在 Service。

#### 调用关系（单向）

```text
请求 → Controller → Service → Mapper → 数据库
              ↑         ↑
           参数/返回   实体/结果
```

Controller **不要**直接调 Mapper（初学就养成习惯）。  
Service **不要**解析 HTTP（别依赖 `HttpServletRequest` 除非特殊场景）。

#### 最小骨架（列表查询）

```java
@RestController
@RequestMapping("/ebook")
public class EbookController {
    @Autowired
    private EbookService ebookService;

    @GetMapping("/list")
    public List<Ebook> list() {
        return ebookService.list();
    }
}

@Service
public class EbookService {
    @Autowired
    private EbookMapper ebookMapper;

    public List<Ebook> list() {
        return ebookMapper.selectByExample(null); // 或自定义方法
    }
}

// Mapper：接口 + XML/注解，由 MyBatis 实现（下一小点细讲）
public interface EbookMapper {
    List<Ebook> selectByExample(Object example);
}
```

| 注解 | 落在哪 |
|------|--------|
| `@RestController` | Controller |
| `@Service` | Service |
| `@Mapper` / `@MapperScan` | Mapper 接口（持久层框架接入时） |
| `@Autowired` | 注入下一层 |

案例：列表接口要加「按名称模糊查」——参数在 Controller 收齐 → Service 拼查询条件 → Mapper 执行 SQL。业务变复杂时，逻辑堆在 Service，Controller 保持瘦。

> 补：请求/返回常再包成 Req/VO（如 `EbookReq`），避免把表实体直接暴露给前端；详见后续「DTO 与实体」。持久层细节进下一小点 MyBatis CRUD。

### 3.1 练习 ★★★★★

1. 三层各自一句话职责？  
2. Controller 能否直接调 Mapper？为何？  
3. `@Service` 标在哪一层？  
4. 调用顺序从外到内？  
5. entity 大致是什么？  

**答案**

1. 接 HTTP；写业务；访问数据库。  
2. 不建议；破坏分层，业务与持久耦合到接口层。  
3. Service（业务层）。  
4. Controller → Service → Mapper → DB。  
5. 与表对应的 Java 对象。  
