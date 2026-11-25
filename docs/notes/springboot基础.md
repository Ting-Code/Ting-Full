# Spring Boot 基础笔记

> 边学边写；对照实战课与仓库。正文只写技术与练习。

## 第1章 课程导学

### 1.1 课程介绍、环境与源码学习 ★★★

**是什么**

第1章是导学，不写业务代码。先对齐四件事：为什么要学 Spring Boot、这门课会做成什么项目、开发环境要准备什么、怎样对照源码和提交记录来学。

**为什么**

Spring Cloud 建立在 Spring Boot 之上（可以记成：Cloud ≈ Boot + 一批组件）。先把 Boot 学扎实，后面微服务才有地基。先看全貌，再进第2章搭工程，方向会更清楚。

#### 为什么先学 Boot

招聘里对 Spring / Spring Boot 的要求通常仅次于 Java 语言本身，普遍要求能用来拆业务、落地功能，而不是只听说过。

相对传统 SSM 手工搭架构，课程强调 Boot 的几项收益：

1. 创建工程快，不用从依赖和 XML 慢慢拼。
2. 集成 Spring 生态（事务、AOP、异步等）主要靠引入 starter 再启用。
3. 约定大于配置，默认就能启动，再按需改配置。
4. 它是微服务的基础，大中小公司都常用。

全栈能力会降低沟通成本，也更容易看清前后端和部署的全貌。本课后端是重点；前端部分你已有基础，可对照着看，不必当新语言重学。

#### 课会做成什么

做一个前后端分离的知识库：电子书维护、分类、无限级文档树、阅读量与点赞统计。业务故意选得简单，方便把精力放在技术上；知识库/博客类项目技术通用，做完也可以当成自己的作品。

开发主流程：设计 → 前后端开发并打通 → 打包 → 部署。工程上会用到 Git、Maven，以及把应用发布到服务器。

课内演示技术栈是 **Spring Boot 2.4 + Vue 3 全家桶**（脚手架、路由、状态管理、UI 组件）。

> 补：你的仓库 Ting-Full 是 **Spring Boot 3.3.5 + Java 21**，前端是 React。跟课学思路和接口分层时，注意版本差异（例如 Boot 3 的包名、配置和 JDK 要求都与 2.4 / JDK 8 不同）。Vue 部分可略看。

#### 开发环境

| 软件 | 课程演示 | 说明 |
|------|----------|------|
| IDEA | Ultimate 或 Community | Community 免费、插件较少；Ultimate 收费但可试用。正式课里会演示常用操作。 |
| MySQL | 8.0 | **5.7 也可以**，课程代码不用改。 |
| Git | Windows 安装 Git | 后面提交、看历史都会用到。 |
| Node.js | 约 16（有 npm 即可） | 主要为了课内前端工程。 |
| JDK | 课程演示 Oracle JDK 8 | 未使用特殊 JDK 特性，其它发行版一般也可。 |

> 补：跑 Ting-Full 请用 **JDK 21**，不要用 JDK 8 去编译这个仓库。

部署阶段传文件、在服务器执行命令，课程里在 IDEA 内完成，不必先另装一套远程工具。

#### 怎么对照源码学习

1. 把可对照的源码用 IDEA 打开。
2. 在 Git 历史里按**章节小节**查找提交（例如学到 2-2，就看标题对应 2-2 的提交）。有的小节一次提交，有的会提交多次，是按步骤拆的。
3. 以后要回忆「某项能力是怎么集成的」，在历史里搜关键字（如 Redis），用 diff 看那一次加了哪些依赖和配置。
4. 自己的练习也建议按小节提交，方便回看和对比。

### 1.1 练习 ★★★

1. 为什么学 Spring Cloud 之前要先学 Spring Boot？
2. 相对传统 SSM，Boot 在搭项目上的主要收益是什么？
3. 本课为什么选知识库这类业务做项目？
4. 开发环境需要哪些软件？本机已是 MySQL 5.7，要不要必须升到 8.0？
5. 怎样用 Git 提交记录配合某一小节学习？

**答案**

1. Spring Cloud 建立在 Spring Boot 之上，Boot 是学微服务的前提。
2. 创建工程快、用 starter 快速集成生态组件、配置精简、并且是微服务的基础。
3. 业务简单，能把精力放在技术上；这类系统技术通用，做完也可以当作自己的作品。
4. 需要 IDEA、MySQL、Git、Node.js（npm）、JDK。5.7 可以直接用，课程代码不用改。
5. 在 Git 历史里找到该小节对应的提交，按步骤查看；需要时用 diff 看这一步改了哪些文件。

## 第2章 Spring Boot 项目搭建

### 2.1 新建工程、目录结构、入口类与 starter ★★★★★

**是什么**

第2章开始真正搭工程。这一小点先做三件事：用两种方式创建一个最基本的 Spring Boot 项目，看清它的标准目录和关键文件，再用入口类把项目启动起来。

Spring Boot 是在 Spring 之上做的「约定大于配置」脚手架：少写 XML、内嵌 Tomcat、用 **starter** 一次拉齐常用依赖和自动配置。前后端分离时，Boot 项目主要负责提供 HTTP 接口（返回 JSON），不负责返回整页 HTML。第一个接口、日志、配置文件和热部署会在后面的小节讲。

**为什么**

以前搭 SSM，依赖版本、XML、再单独部署 Tomcat，往往要花很久才能跑起来。Boot 的做法是：在官网或 IDEA 里选好依赖，生成工程，入口类直接运行就能起服务。后面要学的 Spring Cloud 也建立在 Boot 之上，所以先把工程创建和启动打通。

#### 两种创建方式

1. 打开 [start.spring.io](https://start.spring.io)。选 Maven、Java、打包方式 **jar**；填写 Group（个人或公司标识）和 Artifact（项目名，课内示例是 `wiki`）；Java 版本课内演示是 **8**；依赖至少添加 **Spring Web**（`spring-boot-starter-web`）。热部署（devtools）这节先不要加，后面单独讲。点生成后下载压缩包，解压，再用 IDEA 打开。
2. 在 IDEA 里：File → New → Project，选择 Spring Initializr。填写的内容和网站是同一套，IDEA 会自动访问同一个生成地址、下载并解压。若当时选不到课内的 Spring Boot **2.4.0**，可以先选当前最新版，工程下来后再到 `pom.xml` 里改 parent 的版本号，并选择 Import Changes。生成失败时，可换个时段再试，或先看下一节的 Maven 镜像配置。

> 补：你的仓库 Ting-Full 是 **Spring Boot 3.3.5 + Java 21**。跟课学生成方式和目录即可；对照仓库时不要用 JDK 8 去编译它，也不必把课内工程强行改成 2.4.0。

#### 标准目录（先认这些）

```
wiki/
├── pom.xml                      # Maven 核心：依赖与父工程
├── mvnw / mvnw.cmd              # Maven Wrapper，一般开发用不到
├── .gitignore                   # 指定不提交的文件
├── HELP.md                      # 可改成 README.md
├── src/main/java/...            # 业务代码
│   └── WikiApplication.java     # 入口类（启动类）
├── src/main/resources/
│   ├── application.properties   # 配置文件（也可用 yml）
│   ├── static/                  # 静态资源，前后端分离时常删掉
│   └── templates/               # 服务端模板（如 Thymeleaf），前后端分离时常删掉
└── src/test/java/               # 单元测试
    └── WikiApplicationTests.java
```

说明：

- `src/main` 与 `src/test` 是 Maven 的标准结构，几乎所有 Maven 项目都是这样。
- `mvnw` 的作用是：即使本机还没装 Maven，也可以按文件里写的版本去下载 Maven。课内提示开发时一般用 IDEA 自带的 Maven 即可，这几个 wrapper 文件通常不必依赖。
- `.idea` 目录和 `*.iml` 是 IDEA 的本地项目文件，不要提交到 Git。后面关联远程仓库时，会用 `.gitignore` 把它们排除掉。
- `pom.xml` 里有一个 `spring-boot-starter-parent`。改 Spring Boot 版本，主要是改这个 parent 的版本号，然后 Import Changes，Maven 会按新版本重新下载依赖。

#### 入口类与启动

入口类习惯命名为 `XxxApplication`（课内是 `WikiApplication`）：

```java
@SpringBootApplication
public class WikiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WikiApplication.class, args);
    }
}
```

`@SpringBootApplication` 是一个组合注解，主要做三件事：自动配置、组件扫描、标识这是一个配置类。`main` 就是普通的 Java 入口，调用 `SpringApplication.run` 后会启动内嵌容器。在类上右键 Run 即可。课内演示大约一秒启动完成，默认端口是 **8080**。创建后什么代码都没改，也能启动，这是 Boot 相对传统搭建的一个明显优点。

入口类所在的包是**扫描根**：Controller 等业务类应放在它下面（或同级子包），否则默认扫描不到。

为什么什么都没配却用了 Tomcat、端口是 8080：因为加了 `spring-boot-starter-web` 后，依赖里会带上嵌入式 Tomcat（embedded），不必事先单独安装 Tomcat。默认端口就是 8080，以后可在配置文件里改。也可以打成 war，放到独立的 Tomcat 里运行，有些公司仍用这种发布方式。

#### starter 是什么

| 概念 | 含义 |
|------|------|
| `spring-boot-starter-parent` | 父 POM：统一依赖版本，子依赖常常不必再写 version |
| `spring-boot-starter-web` | Web 场景：Spring MVC + 内嵌 Tomcat + JSON 等 |
| 其它 starter | 按场景勾选，如 data-redis、validation、devtools |

逻辑是：你声明「我要 Web」，Boot 就按约定自动装配好常用 Bean；需要改端口或行为时，再写配置去覆盖。案例：不加 `starter-web` 时工程也能启动，但没有 HTTP 服务；加上之后才有 DispatcherServlet，才能写 `@RestController`。

> 补：对照 Ting-Full 时，看各模块的 `*Application` 入口类，以及对应 `pom` 里的 starter 列表。

### 2.1 练习 ★★★★★

1. 创建 Spring Boot 工程有哪两种常见方式？它们本质有什么相同点？
2. 为什么创建后什么都没配，却能用 Tomcat 在 8080 端口启动？
3. `@SpringBootApplication` 大致做哪几件事？
4. 业务类为什么要放在入口类所在包或其子包下？
5. 哪些文件一般不要提交到 Git？若要改 Spring Boot 版本，通常改 `pom.xml` 的哪一处？

**答案**

1. 一种是打开 start.spring.io 填写后下载压缩包，再用 IDEA 打开；另一种是在 IDEA 里用 Spring Initializr 新建项目。两者本质都是访问同一个生成地址来得到工程。
2. 因为加入了 `spring-boot-starter-web`，依赖里自带嵌入式 Tomcat，默认端口就是 8080，不必单独安装容器。
3. 自动配置、组件扫描、标识这是一个配置类（它是一个组合注解）。
4. 默认只扫描入口类所在包及其子包，放在外面会被漏掉，接口或组件就不会生效。
5. `.idea`、`*.iml` 这类 IDEA 本地文件不要提交，构建产物 `target` 也应忽略。改版本通常改 `spring-boot-starter-parent` 的版本号，然后重新导入依赖。

### 2.2 项目初始配置 ★★★★

**是什么**

工程能创建之后，先做初始配置，再写业务代码。这一小点做五件事：统一 UTF-8、指定项目 JDK、让 Maven 走国内镜像并使用自己的本地仓库、用 Git 做第一次提交、配置乱了就重建 IDEA 的工程文件。

**为什么**

Windows 上文件经常按 GBK 保存。从别处拷来的配置或源文件，用 UTF-8 打开就会乱码，反过来也一样。别人导出的工程往往没有绑定你本机的 JDK，一编译就失败。Maven 默认访问中央仓库，国内下载 jar 常常卡住。代码从第一天交给 Git，后面每做一个小功能就提交一次，才能按历史回看。IDEA 对本工程的选择（JDK、模块）都写在 `.idea` 里，误操作后删掉这个目录再打开，比在菜单里盲改更快。

#### 编码

File → Settings，搜索 Encoding，改这三处：

1. Global Encoding、Project Encoding 改为 **UTF-8**。
2. Properties 文件的默认编码也改为 UTF-8，页面上仍显示 GBK 的项一并改掉。改过的项会标蓝，Apply 后生效。
3. 看右下角：那里是**当前这个文件**的编码。某个文件乱码时，先点开它，再点右下角按真实编码重新打开或转换。只改全局编码，不会自动把已经存错的旧文件变正确。

案例：从别人电脑拷来一份 GBK 的 `application.properties`，中文注释变成问号。点开该文件，右下角从 GBK 换成 UTF-8 并转换，注释才恢复。根因是文件里的字节和 IDEA 打开时用的编码不一致。

#### JDK

File → Project Structure → Project，看 Project SDK。导入的工程这里经常是空的，或指向一台已经不存在的目录。选本机已安装的 JDK；列表里没有就 Add JDK，指向安装目录（该目录下应有 `bin`、`lib`）。

> 补：跟课演示多用 JDK 8。Ting-Full 要选 **JDK 21**。选成 8 后，新语法和 Boot 3 的编译都会直接失败。

#### Maven 与 settings.xml

Settings 里搜索 Maven。开发阶段用 IDEA 自带的 Maven 即可（演示环境是 3.6.1，新版 IDEA 自带的版本更高，不必为了对齐去降级）。勾选 Override，指定一份自己的 `settings.xml`。

这份文件要改两处：

1. `localRepository`：本地仓库目录，依赖会下载到这里，换路径等于换一套已下载的 jar。
2. `mirrors`：把仓库请求转到阿里云，国内下载更快。

```xml
<settings>
  <localRepository>D:/maven/repository</localRepository>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>*</mirrorOf>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

改完后在 IDEA 里重新加载 Maven 工程。现象：不配镜像时进度停在 Downloading；配上之后同一个依赖很快出现在 `localRepository`。已经下过的包不会重复下载。

> 补：`mirrorOf` 写成 `*` 会拦截所有仓库，包括公司私服。有私服时只镜像 `central`，或按公司文档配置，不要用 `*`。

#### 交给 Git

本机先安装 Git，IDEA 才能调用它。菜单：VCS → Enable Version Control Integration → Git。新版 IDEA 里入口可能就叫 Git；左下角工具窗口被藏起来时，点一次左下角图标会重新打开。

文件名颜色表示状态：

| 颜色 | 含义 |
|------|------|
| 红 | 还未交给 Git（未跟踪） |
| 绿 | 已 Add 的新文件，尚未提交 |
| 蓝 | 已跟踪的文件，内容有修改 |
| 灰 | 与上次提交一致。删除后也呈灰色，可用 Revert 撤销删除 |

在 Git 窗口把红色文件全部 Add，再 Commit。第一次提交时业务代码还没改，注释写清楚即可，例如 `init`。提交框里若勾了代码分析、TODO 检查，会明显变慢，这次可以取消。

`.gitignore` 决定哪些文件不进仓库。`.idea`、`*.iml` 是本机工作区文件，不要提交。用生成器创建的工程通常已经写好这些规则，所以启用 Git 后它们不会变红。

案例：把 `.idea` 也 Add 并提交，换一台电脑或换一个 IDEA 版本后，运行配置和编码会互相覆盖。改法是用 `.gitignore` 排除。已经误提交的，要从仓库记录里去掉、本地文件保留；关联远程仓库时再做这一步。

以后每写完一个小功能就提交一次。常见操作会在后面用到时再展开。

#### 配置乱了

先关项目，删除工程根目录下的 `.idea`，再重新打开。模块标错、SDK 指到不存在的路径，这类本机工程状态都在这个目录里。删掉后代码、`pom.xml` 和 Git 历史都还在，IDEA 会按文件重新识别工程。这只修工作区配置，修不了代码本身的编译错误。

### 2.2 练习 ★★★★

1. 为什么把全局编码改成 UTF-8 之后，某一个文件仍可能乱码？要怎么处理？
2. 导入工程后编译失败，提示找不到 JDK，应到哪里指定？
3. `settings.xml` 里和下载速度直接相关的两项是什么？`mirrorOf` 设为 `*` 有什么风险？
4. Git 里红、绿、蓝分别表示什么？第一次提交前为什么要先看 `.gitignore`？
5. 删掉根目录 `.idea` 会丢掉什么，不会丢掉什么？

**答案**

1. 全局编码只影响之后新建和默认打开的方式。这个文件若本来按 GBK 保存，字节和 UTF-8 对不上，仍会乱码。点开该文件，用右下角按真实编码重新打开或转换。
2. File → Project Structure → Project，设置 Project SDK；列表里没有就 Add JDK，指向本机安装目录。
3. `localRepository` 决定 jar 下到哪，`mirrors` 里的阿里云地址决定从哪下。`mirrorOf` 为 `*` 时所有仓库请求都被镜像拦住，公司私服会访问不到。
4. 红是未跟踪，绿是已 Add 未提交的新文件，蓝是已跟踪但有修改。`.gitignore` 用来排除 `.idea`、`*.iml` 这类本机文件，避免把工作区配置提交进仓库。
5. 丢掉的是 IDEA 对本工程的本地配置（SDK、模块、运行配置等）。代码、`pom.xml` 和 Git 历史都还在。

### 2.3 关联 Git 远程仓库 ★★★★

**是什么**

上一小点已经在本地做了第一次提交。这一小点把本地仓库挂到远程，并把代码推上去。流程是：在托管网站建空仓库 → 本机配好 SSH → `git remote add` 关联地址 → `push` 推送 → 补一个远程能识别的 `README.md`。

**为什么**

代码只在本机时，换电脑或磁盘损坏就会丢。远程仓库是备份，也是以后对照章节提交、`git clone` 拉代码的入口。国内访问国外托管站点常很慢，跟课和日常练习优先用国内的 Git 托管；公司一般会给你私有仓库地址。

#### SSH 密钥

本地和远程有两种认证方式：每次输入用户名密码，或先配好 SSH。一台电脑一个密钥即可，配好后同一账号下多个项目都不用再输密码。

常见步骤：

1. 本机安装 Git 后，用 Git GUI：Help → Show SSH Key，复制公钥内容。没有密钥时，按托管网站提示先在本机生成一对。
2. 打开托管网站的用户设置 → SSH 密钥管理，粘贴公钥并起一个本机名字（如 `home-pc`）。
3. 之后 `git` 命令走 SSH 地址（`git@...`），不再弹账号密码。

> 补：公钥贴到网站，私钥留在本机、不要提交进仓库。公司电脑和个人电脑应各加一把密钥。

#### 建空仓库并关联

在托管网站新建仓库：名字定好后通常不能改；描述可后改；选私有。创建时不要勾选「用 README 初始化」。勾了之后远程已有提交，本地再 push 时容易和远程历史冲突；空仓库页面上会直接给出两行关联命令。

本地已有代码时，在工程根目录打开终端，按页面提示执行（地址换成你的仓库）：

```bash
git remote add origin git@example.com:your-name/wiki.git
git push -u origin master
```

说明：

1. `origin` 是远程的常用别名。以后写 `origin`，不必每次敲完整 URL。用 `git remote -v` 可查看当前关联。
2. `-u` 会把本地当前分支和远程分支绑在一起。之后在同一分支上直接 `git push` / `git pull` 即可。
3. 新仓库默认分支有的叫 `main`，有的叫 `master`。以页面提示和 `git branch` 显示为准，两边名字要对齐。

案例：本地已有 `init` 提交，远程却勾了「初始化 README」。第一次 `push` 会被拒绝，因为两边历史不一致。改法是建空仓库再关联，或先 `pull --rebase` 再推（本阶段优先用空仓库，少踩坑）。

#### 推送后补 README

远程首页一般只认 `README.md`。生成器自带的 `HELP.md` 若在 `.gitignore` 里，远程不会出现项目说明。

课内做法：

1. 若 `HELP.md` 被忽略，从 `.gitignore` 去掉对应规则，或直接新建 `README.md`。
2. Add → Commit → Push。
3. 不需要的 `HELP.md` 可以删掉。删除类文件时，IDEA 可能提示「安全删除 / 查找引用」；删 Markdown 说明文件通常不必勾。

新建文件时若弹出「是否交给 Git」，可勾选「以后默认 Add」，之后新文件会直接变绿。

#### 提交说明与 Log

提交说明写清楚本次改了什么。课内习惯在前面加章节标记，方便在历史里对照小节，例如 `2-4 增加 README，删除 HELP`。正式项目里更常见的是用迭代号或任务号，例如 `1.0.3 修复登录超时`。

在 IDEA 的 Git Log 里看本地分支和 `origin/xxx`：

| 现象 | 含义 | 下一步 |
|------|------|--------|
| 本地与 `origin` 在同一提交 | 已同步 | 无 |
| 本地在更前面，`origin` 落在后面 | 本地有未推送提交 | `push` |
| `origin` 在更前面 | 远程有别人推的新提交 | 先 `pull` 再继续改 |

案例：刚 commit 完，Log 里本地 `master` 已前进，`origin/master` 还停在上一笔。此时远程网页刷新不到新文件，必须再 Push 一次，两边指针才会重合。

别人要拿代码：用托管页给出的地址执行 `git clone`。本仓库 Ting-Full 已经关联远程，对照时看 `git remote -v` 和提交历史即可，不必为跟课再建一套同名远程。

### 2.3 练习 ★★★★

1. 本地已有提交时，新建远程仓库为什么不要勾选「用 README 初始化」？
2. 为什么建议配 SSH，而不是每次输入用户名密码？
3. `git remote add origin ...` 和 `git push -u origin master` 各做了什么？
4. 为什么远程首页常常看不到 `HELP.md`，而要写 `README.md`？
5. Log 里本地分支跑到 `origin` 前面，说明什么？要做什么？

**答案**

1. 勾选后远程已有一次提交，本地再 push 容易因历史不一致被拒绝。空仓库会给出清晰的关联命令，第一次推送更直接。
2. 一台电脑配一把密钥后，同一账号下多个项目都不用再输密码；比反复输入账号更省事，也少把密码留在终端历史里。
3. 前者给远程地址起别名 `origin`；后者把本地当前分支推到远程，并用 `-u` 建立跟踪关系，之后可直接 push/pull。
4. 多数托管站点只把根目录的 `README.md` 当作项目说明。`HELP.md` 既不是约定文件名，还可能被 `.gitignore` 排除。
5. 说明本地有已提交但未推送的改动。执行 `push`，让 `origin` 与本地对齐。

### 2.4 启动日志优化 ★★★

**是什么**

工程能启动之后，先把启动时看到的日志整理清楚。这一小点做三件事：用 `logback-spring.xml` 改控制台样式并分流到文件、在入口类打印更醒目的「启动成功」和访问地址、用 `banner.txt` 换掉默认的 Spring 字符画。顺带把本地 `log/` 目录加入 `.gitignore`。

**为什么**

默认日志又长又密，启动成功不明显，排错时也不好扫。统一格式后，级别、类名、行号一眼能对上；错误单独进文件，以后查线上问题更快。改端口、加提示文案，也方便自己和同事确认「当前到底起在哪个地址」。

#### logback-spring.xml

把配置文件放在 `src/main/resources/` 下，Boot 会自动加载。

| Boot 版本 | 推荐文件名 |
|-----------|------------|
| 2.2 | `logback.xml` |
| 2.3 及以后（含课内 2.4、你仓库的 Boot 3） | `logback-spring.xml` |

控制台 Pattern 决定每一列显示什么。课内示例把时间缩成「秒.毫秒」，再跟级别、类名、行号、线程、正文，例如：

```xml
<Pattern>%d{ss.SSS} %highlight(%-5level) %blue(%-30logger{30}:%-4line) %thread %msg%n</Pattern>
```

同一文件里还可挂文件输出：

1. 控制台：INFO 及以上，方便开发时看。
2. `./log/error.log`：只收 ERROR。
3. `./log/trace.log`：更全量的跟踪日志。

路径用 `./log`（相对工程目录）。Windows / macOS / Linux 都写成正斜杠即可；不要写成反斜杠。

案例：启动后工程根目录出现 `log/`，但 IDEA 项目树没刷新。在项目树右键刷新后就能看到。`log/` 是本机运行产物，应在 `.gitignore` 里加 `/log/`，不要提交。

> 补：公司项目常按环境切不同日志级别（dev 打 DEBUG、prod 打 INFO）。`logback-spring.xml` 支持 `<springProfile>`，比普通 `logback.xml` 更贴 Boot；细节后面用到再展开。

#### 入口类打印启动成功

默认启动成功那一行不够醒目。在 `main` 里拿到 `Environment`，读出 `server.port`，自己打两行日志：

```java
@SpringBootApplication
public class WikiApplication {
    private static final Logger LOG = LoggerFactory.getLogger(WikiApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WikiApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }
}
```

在 `application.properties`（或 yml）里改端口，例如：

```properties
server.port=8880
```

再启动时，最后会打出带 `8880` 的地址。`env.getProperty("...")` 可以读任意配置项，后面也可以打印测试地址或提示文案。配置文件整节会在后续小节讲；这里先会改 `server.port`。

#### banner.txt

启动时 Spring 默认会打印字符画 Logo。在 `src/main/resources/` 下新建固定文件名 **`banner.txt`**，内容换成自己的字符或公司标识，重启即生效。网上有 ASCII / 字符画生成工具，把生成结果粘进去即可。

不想显示 Banner 时，可在配置里写 `spring.main.banner-mode=off`（课内这节以自定义为主）。

#### 提交习惯

每改完一块就提交一次，例如：增加 logback → 改启动文案 → 改 banner。以后在 Git 历史里搜「启动」「logback」，就能精确看到当时改了哪些文件，拷到新项目也方便。

### 2.4 练习 ★★★

1. Boot 2.4 / 3.x 下日志配置文件通常叫什么？放在哪个目录？
2. 为什么要把 `log/` 写进 `.gitignore`？
3. 入口类里怎样打印出当前启动端口？端口本身一般改哪项配置？
4. 自定义启动字符画要新建什么文件？文件名能否随便起？
5. 为什么「改一点就提交一次」对以后查问题有帮助？

**答案**

1. 通常叫 `logback-spring.xml`，放在 `src/main/resources/`。
2. 它是本机运行产生的日志文件，提交进仓库没有意义，还会造成无意义的 diff。
3. `SpringApplication.run` 返回的上下文里取 `Environment`，再 `env.getProperty("server.port")`。端口一般改 `server.port`。
4. 新建 `src/main/resources/banner.txt`。文件名是约定，不能随便起，否则 Boot 不会当成 Banner 来读。
5. Git 历史可以按关键字找到那一次改动，diff 里能直接看到改了哪些文件，方便复用到新项目。

### 2.5 第一个接口与 HTTP Client 测试 ★★★★★

**是什么**

这一小点把「能启动」推进到「能提供接口」：在 `controller` 包写一个 Hello World，弄清 `@RestController` 与请求 Mapping、组件扫描范围，再用 IDEA 自带的 HTTP Client（`.http` 脚本）测 GET/POST，并做简单断言。浏览器只能方便地发 GET，POST 和带断言的回归更适合放在 IDE 里。

**为什么**

前后端分离后，后端的交付物就是 HTTP 接口（多返回 JSON 或字符串），不是整页 HTML。第一个接口用来打通「注解 → 扫描 → URL → 响应」整条链路；405、404 这类状态码后面天天会碰到，现在就要能对上原因。

#### 最小接口

在启动类所在包下建 `controller` 包，再新建类（课内示例 `TestController`）：

```java
@RestController
public class TestController {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
```

说明：

1. `@RestController` = `@Controller` + `@ResponseBody`：方法返回值直接写进 HTTP 响应体（字符串或 JSON），**不会**去解析成页面模板。
2. 只用 `@Controller` 时，返回的 `"hello"` 会被当成视图名，去找名为 hello 的页面；前后端分离项目里基本不用这种方式。
3. `@RequestMapping("/hello")` 里的路径拼在主机和端口后面，例如端口 `8880` 时访问 `http://127.0.0.1:8880/hello`。

#### REST 印象与四种 Mapping

常用四种 HTTP 方法，常和增删改查对应：

| 方法 | 注解 | 常见用途 |
|------|------|----------|
| GET | `@GetMapping` | 查询 |
| POST | `@PostMapping` | 新增 |
| PUT | `@PutMapping` | 修改 |
| DELETE | `@DeleteMapping` | 删除 |

REST 风格常把资源写进路径，例如用 `GET /user/1` 查 id=1，而不是只写 `/user?id=1`。路径参数怎么绑，后面再细讲；这里先有印象。

`@RequestMapping` 不写 method 时，理论上多种方法都能打进来。要限制只允许 GET，改成 `@GetMapping("/hello")`，或：

```java
@RequestMapping(value = "/hello", method = RequestMethod.GET)
```

案例：方法上是 `@PostMapping("/hello")`，却用浏览器地址栏访问（浏览器发的是 GET）→ 返回 **405** Method Not Allowed。含义是：URL 能匹配到接口，但**请求方法不被允许**。改回 `@GetMapping`，或改用能发 POST 的工具再测。

#### 组件扫描与入口类位置

`@SpringBootApplication` 里带了组件扫描：默认只扫描**入口类所在包及其子包**。

案例：把 `WikiApplication` 从 `com.jiawa.wiki` 挪到 `com.jiawa.wiki.config`，而 `TestController` 仍在 `com.jiawa.wiki.controller`。二者是平级包，没有「入口包包含 controller」的关系 → 扫不到 Controller → 访问 `/hello` 得 **404**（没有这个接口）。

改法：在入口类上显式扫描项目根包：

```java
@ComponentScan("com.jiawa")
@SpringBootApplication
public class WikiApplication { ... }
```

扫描多个包时写成数组：`@ComponentScan({"com.jiawa", "com.test"})`。不要写成过于宽泛的 `com`，否则可能扫到第三方库里的类，引发奇怪错误。

> 补：Ting-Full 各模块入口类仍在业务根包下时，子包 `controller` 一般能被默认扫到；只有把入口类单独挪到 `config` 这类平级包时，才需要补 `@ComponentScan`。

#### 用 HTTP Client 测接口

浏览器测 GET 要切窗口，且不便发 POST。IDEA 自带 HTTP Client：在工程里建目录（如 `http`），新建后缀为 **`.http`** 的文件。

```http
### 测 GET
GET http://127.0.0.1:8880/hello

### 测 POST（示例）
POST http://127.0.0.1:8880/hello/post
Content-Type: application/x-www-form-urlencoded

name=test
```

行首的绿色三角可单独跑某一段；一个文件里用 `###` 隔开多个请求。Live Template（如课内的 `gtr`）可快速生成 GET 骨架，可按自己习惯配置。

响应断言（类似极简单元测试）：

```http
GET http://127.0.0.1:8880/hello

> {%
client.test("hello", function() {
  client.assert(response.status === 200, "返回码不是 200");
  client.assert(response.body === "Hello World!", "结果验证失败");
});
%}
```

返回的是纯文本时，不要按 JSON 去取字段，否则断言脚本会中断。测试记录会出现在 IDE 的 HTTP 相关目录里，一般不必当业务代码提交；`.http` 脚本本身可以提交，方便以后回归。

IDEA 可打开 Optimize imports / auto import，去掉未使用的 import，提交前少一次手工清理。

### 2.5 练习 ★★★★★

1. `@RestController` 和 `@Controller` 在返回值处理上有什么差别？前后端分离通常用哪个？
2. 浏览器访问接口得到 405，通常说明什么？和 404 怎么区分？
3. 为什么把入口类挪到 `config` 包后，同模块下的 `controller` 可能突然 404？怎么修？
4. `@RequestMapping` 与 `@GetMapping` 在「允许的 HTTP 方法」上有什么差别？
5. 为什么用 IDEA HTTP Client（`.http`）比只靠浏览器测接口更合适？

**答案**

1. `@RestController` 把返回值写入响应体；`@Controller` 常把返回值当视图名去找页面。前后端分离通常用 `@RestController`。
2. 405：路径匹配到了，但方法不允许（例如接口只收 POST，浏览器却发 GET）。404：根本没有匹配到这个接口。
3. 默认只扫描入口类所在包及子包；入口在 `config`、Controller 在平级的 `controller` 时扫不到。在入口类上加 `@ComponentScan("项目根包")`。
4. 不写 method 的 `@RequestMapping` 更宽松；`@GetMapping` 只允许 GET。
5. 不用来回切浏览器；能方便发 POST；还能在同一文件里写多个请求和断言，便于回归。

### 2.6 配置文件与自定义配置项 ★★★★★

**是什么**

Spring Boot 启动时会自动读一组约定好的配置文件。既可以覆盖自带项（如 `server.port`），也可以写业务自己的键，再用 `@Value` 注入到代码里。本小点先会认默认文件位置、`.properties` / `.yml` 两种写法，以及自定义项和默认值；多环境 profile、配置中心放到后面再用到时展开。

**为什么**

端口、开关、文案若写死在 Java 里，换环境就要改代码、重新打包。配置外置后，同一套代码只换配置。自定义项还能给默认值，避免配置中心漏配时直接启动失败。

#### 会自动识别哪些文件

在 `src/main/resources` 下，Boot 至少会认这四个（课内演示）：

| 位置 | properties | yml |
|------|------------|-----|
| `resources/` 根下 | `application.properties` | `application.yml` |
| `resources/config/` 下 | `application.properties` | `application.yml` |

同名配置出现在多处时，**更靠 `config/` 目录的会覆盖根目录的**。课内把根目录端口注释掉、在 `config/` 里写 `8881`，启动后生效的就是 `8881`。

`.properties` 用 `server.port=8880`；`.yml` 用冒号和缩进表示层级：

```yaml
server:
  port: 8882
```

两种格式 Boot 都支持，选一种主用即可。不熟 yml 时，可用「properties ↔ yaml」在线转换（搜 spring properties yaml converter），尤其以后碰到数组型配置时更省事。

另外还有一组 **`bootstrap.properties` / `bootstrap.yml`**（同样可放在根或 `config/`）。**纯 Spring Boot 应用默认不会靠它做主配置**；多在 Spring Cloud + 配置中心场景里，用来先连配置中心。本阶段先记住名字，不必深挖。

> 补：同一键在多个文件里都出现时，自己改两个端口各启动一次就能验证谁赢。完整优先级还有「外部 `file:./config`」等，部署章节会再用到。Ting-Full 各模块主要用 `application.yml`。

#### 自定义配置项 + `@Value`

在 `application.properties`（或 yml）里自己写键，不必是 Boot 预定义的名字：

```properties
test.hello=hello
```

在需要用的地方注入：

```java
@RestController
public class TestController {

    @Value("${test.hello}")
    private String testHello;

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!" + testHello;
    }
}
```

启动时容器扫描到 `@Value`，按 `${...}` 去配置里取值。键名写错或漏配且没有默认值时，启动常会直接失败。

给默认值：键名后面加冒号：

```java
@Value("${test.hello:TEST}")
private String testHello;
```

逻辑：配置里有 `test.hello` → 用配置值；没有 → 用 `TEST`。开发可写在本地文件里；生产若改走配置中心，漏配时默认值能兜住一次启动失败（仍应尽快把正式配置补上）。

案例：断言脚本仍期望正文是 `Hello World!`，但接口已拼上自定义值 → HTTP Client 断言失败。改的是业务返回，不是配置坏了；同步改断言或接口即可。

### 2.6 练习 ★★★★★

1. `resources/application.properties` 和 `resources/config/application.properties` 都配了 `server.port` 时，通常谁生效？
2. `.properties` 和 `.yml` 写端口时各长什么样？Boot 是否只认其中一种？
3. `bootstrap.yml` 在纯 Boot 项目里一般扮演什么角色？什么时候才重点用？
4. 怎样把自定义键 `test.hello` 注入到字段里？如何写默认值？
5. 配置里有值和 `@Value` 里写了默认值同时存在时，用哪一个？

**答案**

1. 通常是 `resources/config/` 下的生效，它覆盖根目录同名配置。
2. properties：`server.port=8880`；yml：`server:` 下缩进 `port: 8880`。两种都认。
3. 纯 Boot 默认不靠它做主配置；Spring Cloud 连配置中心、做动态配置时才重点用。
4. `@Value("${test.hello}")`；默认值写成 `@Value("${test.hello:默认值}")`。
5. 优先用配置文件（或配置中心）里的值；只有键不存在时才用冒号后的默认值。

### 2.7 集成热部署 ★★★

**是什么**

热部署（课内用 **spring-boot-devtools**）让改完 Java 代码后不必整应用重启，保存/编译后很快重新加载，再访问接口就能看到新结果。配置分三步：加依赖、打开 IDEA 自动编译、允许「应用运行中也能自动编译」。

**为什么**

前面每改一行返回值都要停掉再启动，浪费时间。开发阶段用热部署可以「改 → 保存/编译 → 马上测」。它只适合本地开发，不要指望靠它在生产热更新。

#### 三步配置

**1. 加依赖（不要手写 version）**

在 `pom.xml` 的 `dependencies` 里增加（与其它 dependency 同级）：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

version 由 `spring-boot-starter-parent` 统一管理。点进 parent 还能看到 BOM 里锁定的具体版本。`optional` 表示尽量不把开发工具传递到依赖你的其它模块。

**2. IDEA：静态自动编译**

Settings 搜索 Compiler，勾选 **Build project automatically**（应用未强调运行时的自动编译）。

**3. IDEA：运行中也允许自动编译**

`Ctrl+Shift+A`（或连按两下 Shift）打开动作搜索，输入 **Registry**，勾选：

`compiler.automake.allow.when.app.running`

新版 IDEA 也可能在 Advanced Settings 里有类似「Allow auto-make … while application is running」的选项，含义相同：应用正在跑时，仍允许自动编译，devtools 才能接到变更并重启类加载。

#### 怎么触发、怎么验证

1. 先正常启动应用，访问接口，记住返回值。
2. 改 Controller 返回字符串（如改成 `Hello 1`），**Ctrl+S** 保存。
3. 控制台会出现一次快速重启（课内约零点几秒），再访问接口应是新文案。

没有保存时，有的切换标签会触发保存，但改完立刻测时养成 **Ctrl+S** 更稳。也可以改完后点一次 **Build / Recompile**，往往比干等自动编译更快。

案例：没加 devtools 时，改返回值并手动 Compile，再访问仍是旧结果 → 必须重启进程。三步配齐后，保存或点 Build，短暂停顿后再访问即新结果。

> 补：devtools 重启的是应用上下文，比重启整个 JVM 快，但改 `pom`、部分配置或新增某些类时仍可能要手动停启。Ting-Full 多模块若未加 devtools，跟课在练习工程里配即可，不必强行改仓库。生产包不要依赖热部署。

### 2.7 练习 ★★★

1. 热部署在课里主要靠哪个依赖？version 为什么常常不写？
2. IDEA 里和热部署相关的两个开关分别解决什么问题？
3. 改完代码后为什么常要 Ctrl+S 或点一次 Build？
4. 热部署适合用在生产环境吗？
5. 怎样快速验证热部署是否生效？

**答案**

1. `spring-boot-devtools`。版本由 `spring-boot-starter-parent` 统一管理。
2. Build project automatically：保存后自动编译；Registry 里运行中允许 auto-make：应用启动后改代码仍能编译，devtools 才能重载。
3. 触发保存/编译后，devtools 才会发现 classpath 变化并做快速重启。
4. 不适合。只用于本地开发提效。
5. 启动后访问接口记下返回值，改返回字符串并保存/编译，再访问，新文案出现且控制台有一次快速重启即生效。

## 第3章 后端架构完善与接口开发

### 3.1 电子书列表查询与统一返回 ★★★★★

**是什么**

第3章开始做真实业务。这一小点（课内 3-6）完成第一条业务查询链路：设计并执行 `ebook` 表 → MyBatis Generator 生成持久层 → 手写 `EbookService` / `EbookController` 提供 `GET /ebook/list` → 用 HTTP Client 测通 → 把返回值收成统一的 `CommonResp`。

前置（课内 3-2～3-5，此处不重讲）：库已建好、MyBatis 已集成、Generator 已能跑。若本地还没表，先执行资料里的建表 SQL。

**为什么**

首页要展示电子书卡片（名称、描述、文档数、阅读、点赞，以及两级分类）。后端先提供列表接口，前端才能渲染。直接返回 `List<实体>` 时，成功/失败形态不统一，前端难以统一处理弹窗和取数；所以用泛型包装类把「是否成功、提示文案、业务数据」固定下来。

#### 表设计（ebook）

对照首页字段，分类存 **ID**（两级各一列），主键与其它表统一用 `bigint`（后面会用雪花算法生成 ID，本小点先手工插入示例数据）：

```sql
drop table if exists `ebook`;
create table `ebook` (
  `id` bigint not null comment 'id',
  `name` varchar(50) comment '名称',
  `category1_id` bigint comment '分类1',
  `category2_id` bigint comment '分类2',
  `description` varchar(200) comment '描述',
  `cover` varchar(200) comment '封面',
  `doc_count` int not null default 0 comment '文档数',
  `view_count` int not null default 0 comment '阅读数',
  `vote_count` int not null default 0 comment '点赞数',
  primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='电子书';
```

执行后刷新库，确认有表和示例行。字符集用 **utf8mb4**。

#### 生成器 → 持久层

在 `generator-config.xml` 里打开（或新增）ebook 表配置，例如：

```xml
<table tableName="ebook" domainObjectName="Ebook"/>
```

已生成过的表可注释掉，避免重复刷。跑 Generator 后得到：`Ebook`、`EbookExample`、`EbookMapper`、`EbookMapper.xml`。改一点就提交一次，方便以后按提交记录回看。

#### Service / Controller / 测试

业务层与接口层手写；可从之前的 Demo 复制再全局替换类名。本小点最初形态：

```java
@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<Ebook> list() {
        return ebookMapper.selectByExample(null); // null = 无额外条件，查全部
    }
}
```

```java
@RestController
public class EbookController {
    @Resource
    private EbookService ebookService;

    @GetMapping("/ebook/list")
    public List<Ebook> list() {
        return ebookService.list();
    }
}
```

`.http` 示例：

```http
GET http://127.0.0.1:8880/ebook/list
```

启动后执行，应返回多条电子书 JSON。调用链：**Controller → Service → Mapper（生成）→ SQL**。

> 补：Ting-Full 商品列表是 `ProductController` → `ProductService` → `BizProductMapper.selectList(...)`（MyBatis-Plus）。分层相同，只是 Mapper 调用方式不同。

#### 统一返回 CommonResp

后面接口会很多，前端需要统一判断成败。新建 `resp.CommonResp<T>`：

| 字段 | 含义 |
|------|------|
| `success` | 业务是否成功，默认 `true` |
| `message` | 提示文案（失败时常有） |
| `content` | 真正的业务数据，类型用泛型 `T` |

改造列表接口：

```java
@GetMapping("/ebook/list")
public CommonResp<List<Ebook>> list() {
    CommonResp<List<Ebook>> resp = new CommonResp<>();
    List<Ebook> list = ebookService.list();
    resp.setContent(list);
    return resp;
}
```

返回形态变为：

```json
{
  "success": true,
  "message": null,
  "content": [ { "id": 1, "name": "...", ... } ]
}
```

约定：以后后端接口外层都是 `CommonResp`，只变 `content`。前端可按 `success` 决定是取 `content` 还是弹出 `message`。

案例：登录失败时 `success=false`，`message="用户名或密码错误"`，`content` 为空；成功则 `content` 里放用户信息。列表成功只设 `content` 即可（`success` 默认 true）。

> 补：Ting-Full 的 `R<T>`（`code` / `message` / `data`）是同一类思想，字段命名不同。课内下一步（3-7）会再把请求参数、返回 VO 从实体里拆开，本小点先打通列表 + 统一外壳。

### 3.1 练习 ★★★★★

1. 电子书表为什么存 `category1_id` / `category2_id`，而不是直接存分类名称？
2. Generator 生成了什么？Service / Controller 为什么还要手写？
3. `selectByExample(null)` 在本小点表示什么？
4. 为什么要把 `List<Ebook>` 改成 `CommonResp<List<Ebook>>`？
5. `CommonResp` 三个字段各干什么？列表成功时通常改哪个？

**答案**

1. 分类会独立成表、可能改名；存 ID 避免改名时大量改电子书行，也方便按分类筛选与关联。
2. 生成实体、Example、Mapper 接口和 XML。Service / Controller 是业务与 HTTP 入口，生成器不负责。
3. 不附加查询条件，查出 ebook 表全部行（示例数据）。
4. 让所有接口外层结构一致，前端可统一处理成功取数和失败提示。
5. `success` 成败，`message` 提示，`content` 业务数据。列表成功时主要 `setContent(list)`。

### 3.2 请求/返回封装与 CopyUtil ★★★★★

**是什么**

列表接口加上按名称模糊查询后，把「进来的查询条件」和「出去给前端的数据」从数据库实体里拆开：用 `req` 包封装请求、`resp` 包封装返回；实体 → 返回对象用 `BeanUtils` / 课内 `CopyUtil` 拷贝。对应课内 3-7 + 3-8。

**为什么**

查询条件会越加越多（name、id、描述…），Controller 方法参数会膨胀。返回若直接甩实体，密码、手机号等敏感字段可能泄露；有时还要少返或多返字段。分层约定：**Controller 尽量只见到 Req/Resp，不要直接对外暴露表实体。**

#### 模糊查询（Example）

Generator 生成的 `EbookExample` 用来拼 WHERE。固定两句 + 条件：

```java
EbookExample example = new EbookExample();
EbookExample.Criteria criteria = example.createCriteria();
criteria.andNameLike("%" + name + "%"); // 左右都加 %
return ebookMapper.selectByExample(example);
```

HTTP 可先写成 `GET /ebook/list?name=Spring`。Spring 会把同名查询参数绑到方法参数或后面封装类的同名属性上。

#### 请求封装 Req

参数变多时，新建 `req` 包，例如 `EbookQueryReq`，只保留查询需要的字段（如 `id`、`name`），不要把整张表字段都塞进来。

```java
public class EbookQueryReq {
    private Long id;
    private String name;
    // getter/setter
}
```

```java
@GetMapping("/ebook/list")
public CommonResp<List<EbookQueryResp>> list(EbookQueryReq req) {
    // …
}
```

```java
// Service 里
criteria.andNameLike("%" + req.getName() + "%");
```

属性名与查询参数名一致即可自动映射。`?name=Spring` 会进 `req.name`。

#### 返回封装 Resp

新建 `EbookQueryResp`（可从实体拷贝再删减）。无敏感字段时可以与实体字段相同；有密码、身份证等则**不要**放进 Resp。

Controller / 对外返回类型改为 `List<EbookQueryResp>`（再包在 `CommonResp` 里），避免 Controller 层直接出现表实体 `Ebook`。

#### 对象拷贝：BeanUtils → CopyUtil

实体列表要变成 Resp 列表时，逐字段 `set` 太烦。Spring 提供：

```java
EbookQueryResp resp = new EbookQueryResp();
BeanUtils.copyProperties(ebook, resp); // source → target，同名属性拷贝
```

列表就要循环。课内抽成 `CopyUtil`：

```java
public class CopyUtil {
    public static <T> T copy(Object source, Class<T> clazz) { /* newInstance + BeanUtils */ }

    public static <T> List<T> copyList(List source, Class<T> clazz) { /* 循环 copy */ }
}
```

Service 里最终可写成：

```java
List<Ebook> ebookList = ebookMapper.selectByExample(example);
List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);
return list;
```

单个对象：`CopyUtil.copy(ebook, EbookQueryResp.class)`。

案例：拷贝后临时 `resp.setId(123L)` 再访问，若返回里 id 变成 123 且其它字段仍有值，说明 `copyProperties` 生效。测完删掉测试代码再测一次。

> 补：Ting-Full 写接口常用 `ProductSaveRequest` 等 DTO + 校验注解；列表有的接口仍直接返回实体（可再演进成 VO）。MP 项目也常用 MapStruct；课内 `CopyUtil` 够用即可。注意 `BeanUtils` 只拷贝同名、兼容类型的属性，名字不一致要手写转换。

### 3.2 练习 ★★★★★

1. 为什么查询条件要用 `EbookQueryReq`，而不是在方法上无限加 `String name, Long id, …`？
2. 为什么返回要用 `EbookQueryResp`，而不是直接 `List<Ebook>`？
3. `andNameLike` 时百分号写在哪里？只写一边表示什么？
4. `BeanUtils.copyProperties(a, b)` 两个参数分别是什么？
5. `CopyUtil.copy` 和 `copyList` 各解决什么重复劳动？

**答案**

1. 条件会迭代增加，封装成类后 Controller/Service 签名稳定，也方便校验和扩展。
2. 对外数据与表结构解耦，可隐藏敏感字段，也可增减展示字段。
3. 加在传入值两侧：`%` + name + `%`。只左边或只右边分别是后缀匹配、前缀匹配。
4. 第一个是源对象，第二个是目标对象（同名属性从源拷到目标）。
5. `copy` 省掉单个对象的多次 set；`copyList` 省掉整段 for 循环拷贝列表。

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
