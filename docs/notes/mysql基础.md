# MySQL 基础

> 结构：要点（含逻辑）→ 案例（现象/违规 → 改法）→ 练习。去掉口播，不删逻辑与案例。条目尽量 3 或 5。

## 第1章 选型

### 1.1 SQL vs NoSQL ★★★

| | SQL（关系型） | NoSQL（非关系型） |
|--|---------------|-------------------|
| 含义 | 用 SQL 操作数据 | Not Only SQL（不是「不要 SQL」） |
| 形态 | 库 → 表 → 行/列 | 文档、键值等，模型多样 |
| 常见 | MySQL、PostgreSQL | MongoDB、Redis |

- 实体有关联、要事务 → 业务库用 **MySQL**  
- 缓存、登录 Token、简单 KV → **Redis**，和 MySQL **分工**，不是互相替换  

### 1.2 关系型：特点与适用 ★★★

| 特点 | 含义 |
|------|------|
| 结构化 | 先定表结构；同一列类型一致 |
| ACID | 事务要么全成功，要么全回滚 |
| SQL | 条件查询、聚合、多表关联 |

**适合：** 订单、用户、商品这类「对象之间有关系、且数据必须正确」的业务。  
**案例：** 一笔订单多行明细、一件商品出现在多笔订单 → 用表 + 关联表达。

### 1.3 非关系型：特点与适用 ★★★

| 特点 | 含义 |
|------|------|
| 模型灵活 | 不必事先定死全部字段 |
| 弱化 ACID | 常换更高吞吐，难做银行级强事务 |
| API 不统一 | 各产品自己的命令，不是标准 SQL |

**适合：** 日志/埋点、字段常变的内容、高并发行为轨迹；Redis 做缓存与计数。  
**不适合单独扛：** 下单、支付、库存等强一致核心业务。

### 1.4 选型与定 MySQL ★★★

关系型产品再筛选时，常见五步：生态 → 成熟度 → 扩展能力 → 安全稳定 → 成本与操作系统。原则是**合适比喜欢重要**。

常选 MySQL 的原因：开源免费、主从/读写分离够用、权限与备份完善、招人与资料多。

### 1.5 练习 ★★★

1. NoSQL 全称含义？MySQL 和 Redis 如何分工？  
2. 关系型三个核心特点是什么？  
3. 哪些场景适合 NoSQL？为什么订单库一般仍用关系型？  
4. 选型大致看哪五步？中小项目为何常选 MySQL？  
5. 订单与商品为何适合关系型？  

**答案**

1. Not Only SQL；MySQL 存业务与事务，Redis 做缓存/KV。  
2. 结构化、ACID、SQL。  
3. 日志、结构多变、高并发轨迹等；订单要强一致和关联。  
4. 生态、成熟、扩展、安全、成本/OS；开源且生态够用。  
5. 多实体有关联，需要 JOIN，并用事务保证金额与库存不错乱。  

---

## 第2章 表设计

**顺序：** 先逻辑（存什么、怎么拆表）→ 再物理（引擎、类型、命名）→ 最后才写 CREATE。

### 2.1 表设计流程 ★★★★

| 阶段 | 做什么 | 产出 |
|------|--------|------|
| 业务分析 | 从页面找出对象、属性、关系 | 实体清单 |
| 逻辑设计 | 按范式拆表、定关联 | 表结构草案 |
| 物理设计 | 定引擎、类型、命名 | 可执行的建表方案 |

业务分析三问：有哪些对象？每个对象有哪些描述？谁对谁是一对多？  
本步**只列清单，不写 SQL、不定字段长度**。

| 实体类型 | 例子 |
|----------|------|
| 主数据 | 用户、课程 |
| 明细 | 章节、订单明细 |
| 业务记录 | 评价、笔记 |
| 字典 | 分类、难度 |

**案例：** 「用户给课程写评价」→ 实体：用户、课程、评价；关系：用户 1:N 评价，课程 1:N 评价。

### 2.1 练习 ★★★★

1. 逻辑设计和物理设计各解决什么问题？  
2. 建表前三步依次是什么？  
3. 什么是实体、属性？这一步能直接 CREATE 吗？  
4. 主数据、明细、业务记录、字典各举一例。  
5. 一门课多个章节是什么关系？  

**答案**

1. 逻辑解决「拆哪些表」；物理解决「引擎/类型/命名」。  
2. 业务分析 → 逻辑拆表 → 物理落地。  
3. 对象叫实体，描述叫属性；不能直接 CREATE。  
4. 用户；章节；评价；分类。  
5. 一对多（1:N）。  

### 2.2 宽表 ★★★★

**宽表：** 把多个实体的信息挤进一张表，同一事实在多行里重复保存。

**案例（违规宽表一行长什么样）**

| course_id | title | teacher_name | teacher_title | category_name |
|-----------|-------|--------------|---------------|---------------|
| 1 | MySQL入门 | 张三 | 高级讲师 | 数据库 |
| 2 | Redis入门 | 张三 | 高级讲师 | 数据库 |

同一讲师教 2 门课 → `teacher_title` 存了 2 份。逻辑上「讲师职位」只该有一份真相。

| 异常 | 现象 → 后果 |
|------|-------------|
| 更新异常 | 只改第 1 行职位为「专家」→ 第 2 行仍是「高级讲师」（同人两份职位） |
| 插入异常 | 想先建方向「大数据」，但还没有任何课程行 → 方向插不进（方向绑死在课程行上） |
| 删除异常 | `DELETE … WHERE category_name='数据库'` → 该方向下课程行全部删掉 |

**根因：** 冗余 + 字典/讲师属性绑死在业务表。  
**改法：**

```text
teacher(id, name, title)          -- 职位只存一处
category(id, name)                -- 无课也能先维护
course(id, title, teacher_id, category_id)
```

宽表适合报表、读多写少、刻意少 JOIN；**不适合** OLTP 核心业务（订单、用户资料等频繁改写）。

### 2.2 练习 ★★★★

1. 什么是宽表？根因是什么？  
2. 用课程表各举一例更新、插入、删除异常。  
3. OLTP 为什么少用宽表？报表为什么可以用？  
4. 订单每行存客户姓名，容易哪种异常？怎么改？  
5. 拆表后课程表大概只留什么？  

**答案**

1. 多实体挤一张、信息重复；根因是冗余。  
2. 改职位不一致；新方向插不进；删方向误删课程。  
3. 写入多、异常代价大；报表读多写少可容忍冗余。  
4. 更新异常；改为存 `customer_id`，姓名放用户表。  
5. 课程自身属性 + 各类 `xxx_id`。  

### 2.3 三大范式 ★★★★★

日常设计满足前三个范式通常够用，且必须按 **1NF → 2NF → 3NF** 递进，不能跳级。

口诀：**1NF 管原子，2NF 管完全依赖，3NF 管传递依赖。**

**1NF：列必须原子，不能再拆**

| 违规 | 改法 |
|------|------|
| `contact = '138… / 010-… / a@b.com'` 一格多值 | 拆成 `mobile`、`tel`、`email` 三列 |

**2NF：非主键列必须依赖整个主键（复合主键才容易踩）**

| 违规笔记表 | 问题 |
|------------|------|
| 主键 `(user_id, chapter_id, title)`，却存 `user_score` | 积分只跟用户有关，不依赖「章节+标题」→ 部分依赖 |

改法：`user_score` 放到 `user` 表；笔记表只留笔记自身字段。  
主键只有一列时，一般自然满足 2NF。

**3NF：非主键列不能再依赖其它非主键列（消除传递依赖）**

| 违规课程表 | 依赖链 | 改法 |
|------------|--------|------|
| `course_id → teacher_id → teacher_title` | 职位经讲师间接依赖主键 | `teacher_title` 放到 `teacher` 表 |

通法：违规列拆到独立表，原表保留关联键。业务 OLTP 至少做到 3NF；报表可以再反范式。

### 2.3 练习 ★★★★★

1. 1NF / 2NF / 3NF 各管什么？  
2. 联系方式拼多值，违反哪条？怎么改？  
3. 复合主键笔记表存用户积分，违反哪条？怎么改？  
4. 课程表存讲师职位，违反哪条？怎么改？  
5. 订单明细主键 `(order_id, product_id)` 却存商品名称，违反哪条？  

**答案**

1. 列原子；完全依赖整个主键；无传递依赖。  
2. 1NF；拆成多列。  
3. 2NF；积分进用户表。  
4. 3NF；职位进讲师表。  
5. 2NF；名称进商品表，明细只留 `product_id`。  

### 2.4 逻辑建模 ★★★★

在业务分析和范式之后，把实体落成「有哪些表、各存什么、怎么关联」。这一步仍不定具体 SQL 类型。

| 原则 | 做法 | 为什么 |
|------|------|--------|
| 一实体一表 | 用户、课程各一张主表 | 避免宽表把多实体挤一起 |
| 字典独立 | 方向/分类/难度单独建表；主表只存 id | 无课也能维护；改名只改一处 |
| 可算不存 | 「是否最热」用学习人数排序 | 少一列冗余，避免和真实数据打架 |
| 1:N | 子表存父表 id（如 chapter.course_id） | 一方一条、多方多条的标准挂法 |
| 多对多 | 中间表存两端 id | 两边都是多，直接互存会爆炸 |
| 相似合并 | 用户+讲师 → 用户表 + is_teacher | 属性重合时拆两表易不一致 |

**案例：从页面落到表**

```text
课程表：title, duration, intro, online_at, learn_count, teacher_id, category_id
字典表：direction / category / difficulty（没有课程也能先 INSERT）
关系：course 1:N chapter 1:N section
评价：(user_id, course_id) + 各维度分数
选课中间表：(user_id, course_id)

违规：course 直接存 category_name、teacher_title
改法：category / teacher 独立表 + 关联 id
```

### 2.4 练习 ★★★★

1. 逻辑建模在哪两步之后？  
2. 为什么方向、分类要字典表？  
3. 为什么「是否最热」可以不单独存？  
4. 用户和讲师为什么合并？  
5. 写出商品、订单、订单明细三张表名和两个外键。  

**答案**

1. 业务分析、范式之后。  
2. 避免宽表异常，且无课也能维护字典。  
3. 可用学习人数计算，减少冗余列。  
4. 属性重合；合并后加「是否讲师」，避免两张表数据不一致。  
5. `product`、`order`、`order_item`；如 `order.user_id`、`order_item.order_id`。  

### 2.5 反范式 ★★★

按 3NF 拆得很细后，一次查询可能要 JOIN 很多张表，高频列表会变慢。

**反范式：** 在热点表上故意保留一点冗余，用空间换查询时间。

**案例**

```text
规范：section JOIN chapter JOIN course 才能拿到课名
反范式：section 表多一列 course_title（冗余）
查目录：直接读 section，少两次 JOIN
代价：改 course.title 时，必须同步更新 section.course_title
```

原则：先做成 3NF，出现慢查询再改；只动热点读表；主表、字典、核心写表少动。  
适合读多写少；不适合核心字段天天修改的场景。

### 2.5 练习 ★★★

1. 范式过细，查询上会有什么问题？  
2. 反范式牺牲什么、换来什么？  
3. 设计顺序是先范式还是先反范式？  
4. 哪些表通常不要随便反范式？  
5. 订单明细冗余商品名称算不算反范式？改名要注意什么？  

**答案**

1. 多表 JOIN，高频查询变慢。  
2. 牺牲一致性维护成本，换查询性能。  
3. 先 3NF，再按需反范式。  
4. 主表、字典、频繁改写的核心业务表。  
5. 算；改商品名时要同步明细里的冗余名称。  

### 2.6 存储引擎 ★★★★★

物理设计第一步：为表选择存储引擎。它决定数据怎么存、怎么锁、是否支持事务。  
无特殊需求时，业务表统一使用 **InnoDB**。

| 引擎 | 事务 | 特点 | 适用 |
|------|------|------|------|
| InnoDB | 有 | 行锁、MVCC、聚簇索引 | 业务默认 |
| MyISAM | 无 | 表锁、堆表 | 新业务不用 |
| Archive | 无 | 只增查、压缩 | 日志流水 |
| Memory | 无 | 数据在内存，重启丢失 | 临时结果 |
| CSV | 无 | 类似文件交换 | 跨系统导入导出 |

**InnoDB 必记三点**

1. 支持事务 ACID；同一事务不要混用非事务引擎表，否则回滚时那些表改不回去。  
2. 聚簇索引：数据按主键顺序存放；二级索引叶子存主键，再回表。主键宜短且有序，常用自增 id；业务唯一号建 UNIQUE。  
3. 行锁 + MVCC，适合高并发读写。

```sql
SHOW TABLE STATUS LIKE '表名';
CREATE TABLE ... ENGINE=InnoDB;
```

### 2.6 练习 ★★★★★

1. 存储引擎解决什么问题？  
2. 业务表为什么默认 InnoDB？  
3. InnoDB 和 MyISAM 在事务、锁上有何区别？  
4. 什么是聚簇索引？主键怎么选？  
5. 日志表只追加不改历史，更倾向 Archive 还是 InnoDB？  

**答案**

1. 决定物理存储、锁粒度和是否支持事务。  
2. 需要事务、行锁和高并发。  
3. InnoDB 有事务+行锁；MyISAM 无事务+表锁。  
4. 数据按主键顺序存；常用自增 id 作主键，业务号用 UNIQUE。  
5. 纯追加可用 Archive；还要事务或复杂查询则用 InnoDB。  

### 2.7 字段类型 ★★★★★

物理设计第二步：为每列选择合适类型。原则是**够用即可**（不是越大越好）。

| 类别 | 怎么选 | 典型用法 |
|------|--------|----------|
| 整数 | TINYINT→BIGINT，能小则小；UNSIGNED 扩大正数范围；`INT(11)` 的 11 只是显示宽度，不缩小存储 | 状态用 TINYINT；主键外键常用 BIGINT |
| 小数 | DECIMAL 精确；FLOAT/DOUBLE 非精确 | 金额必须 DECIMAL |
| 时间 | DATE 只要日期；TIME 可表示超过 24 小时的时长；DATETIME 不涉及时区且范围大；TIMESTAMP 随时区显示且大约到 2038 | 跨时区或 2038 之后优先 DATETIME |
| 字符串 | CHAR 定长；VARCHAR 变长最常用；TEXT 存大段文本 | 标题、昵称用 VARCHAR |

**案例：为什么金额不能用 DOUBLE**

```sql
CREATE TABLE t_num (d DOUBLE, m DECIMAL(10,2));
INSERT INTO t_num VALUES (11.11, 11.11), (11.11, 11.11);
SELECT d, d+d, m, m+m FROM t_num;
-- d+d 可能出现 22.220000000000003 这类误差
-- m+m 仍是 22.22
```

逻辑：对账、支付必须精确 → 用 DECIMAL；统计近似值才考虑浮点。

### 2.7 练习 ★★★★★

1. 状态码只要 0～255，用 TINYINT 还是 `INT(2)`？为什么？  
2. 金额为什么用 DECIMAL？  
3. TIME 为什么可以大于 24 小时？  
4. DATETIME 和 TIMESTAMP 主要差别？  
5. CHAR 和 VARCHAR 差别？  

**答案**

1. 用 TINYINT；`INT(2)` 仍然占 4 字节，范围不变。  
2. DECIMAL 精确，对账不能丢精度。  
3. TIME 还可以表示时长或时间间隔，不限于一天内的钟点。  
4. DATETIME 不按时区转换、范围大；TIMESTAMP 显示随时区变，大约到 2038。  
5. CHAR 定长占满；VARCHAR 按实际内容占空间。  

### 2.8 字段选型 ★★★★

在认识类型之后，还要按 InnoDB 和业务把列定下来。

**主键与关联**

| 做法 | 原因 |
|------|------|
| 自增 id 作数据库主键 | 短、有序，利于聚簇索引 |
| 业务号建 UNIQUE | 订单号、标题等不当乱序字符串主键 |
| 表之间用 xxx_id 关联 | 比用名称关联更小、更快 |
| 展示名称靠 JOIN 取 | 改一次主数据即可全局一致 |

**选类型原则**

1. 优先满足需求的最小类型；非负数可用 UNSIGNED。IPv4 可用 `INT UNSIGNED`。  
2. 备注多数用 VARCHAR，少用 TEXT；ENUM 值常变时改用 TINYINT + 字典表；金额用 DECIMAL。  

**案例：** 订单表建议 `id BIGINT` 主键、`order_no` 唯一、`amount DECIMAL(10,2)`、`user_id`、`status TINYINT`。

### 2.8 练习 ★★★★

1. 为什么推荐自增 id 作主键？业务号放哪里？  
2. 课程表存 teacher_id 而不是讲师昵称，好处是什么？  
3. IPv4 为什么可以存成 INT UNSIGNED？  
4. TEXT 相比 VARCHAR 有哪些限制？  
5. 什么时候不建议用 ENUM？  

**答案**

1. 利于聚簇索引顺序写入；业务号建 UNIQUE。  
2. 改昵称只改用户表，其它表通过 id 关联仍一致。  
3. IPv4 可映射为 32 位整数，比字符串更省空间。  
4. 通常只能前缀索引、不便设默认值，排序等操作代价更高。  
5. 枚举值经常增删改、需要频繁 ALTER 时。  

### 2.9 命名规范 ★★★

物理设计最后一步：库名、表名、列名要见名知意，并在团队内统一。不要用中文命名。

| 规则 | 说明 |
|------|------|
| 小写 + 下划线 | 如 `order_item`；Linux 下库表名区分大小写 |
| 避开保留字 | 如 `order`、`group` |
| 长度适中 | 建议不超过 32；多库时可加项目前缀 |
| 临时表 / 备份表 | `tmp_` + 日期；`bak_` + 日期，方便清理 |
| 列名统一 | 全库统一 `user_id`，不要混用 `uid`；外键写成 `{实体}_id` |

**案例：** `订单表` 改为 `order_info`；`group` 改为 `user_group`；`courseTitle` 改为 `course_title`。

第 2 章到此结束：逻辑设计解决拆表，物理设计解决引擎、类型与命名；下一步用 SQL 真正建库建表。

### 2.9 练习 ★★★

1. 为什么推荐小写加下划线？  
2. 临时表和备份表如何命名？  
3. 为什么列名要跨表统一？  
4. 外键列一般怎么命名？  
5. 用保留字做表名会怎样？  

**答案**

1. Linux 下大小写敏感，统一小写少踩坑。  
2. `tmp_` / `bak_` 加日期后缀。  
3. JOIN 和代码映射更简单，类型也易对齐。  
4. `{被引用实体}_id`。  
5. 容易和语法冲突，常需反引号，易出错。  

---

## 第3章 SQL

**顺序：** 先分清语句类型 → 权限（DCL）→ 建表改表（DDL）→ 增删改查（DML）。

### 3.1 SQL 语句分类 ★★★★★

**SQL 是什么**

SQL = Structured Query Language（结构化查询语言）。  
用途只有一件事：让你用语句操作**关系型数据库**里的数据（建库建表、增删改查、管权限、管事务）。

和 Java 的差别：Java 要写「怎么一步步算」；SQL 写「我要什么结果」。  
例如业务说「从用户表里取出 status=1 的人」→ 对应一句 `SELECT … FROM user WHERE status=1`。  
数据库自己决定怎么查，你不用写循环扫表。

**为什么要分类**

同一门 SQL 里，不同语句管的对象不一样：有的管「谁能登录」，有的管「表长什么样」，有的管「表里有哪些行」。  
先分清类别，后面学具体语句才知道自己在改什么、风险在哪。

| 分类 | 全称（记这个） | 管什么 | 常见语句 |
|------|----------------|--------|----------|
| DCL | Data Control Language 数据控制 | **谁能访问**库/表 | CREATE USER、GRANT、REVOKE |
| DDL | Data Definition Language 数据定义 | **库/表的结构**（架子） | CREATE、ALTER、DROP |
| DML | Data Manipulation Language 数据操作 | **表里的行数据**（内容） | SELECT、INSERT、UPDATE、DELETE |
| TCL | Transaction Control Language 事务控制 | **多步操作一起成功或一起失败** | BEGIN、COMMIT、ROLLBACK |

> 补：有的资料把 SELECT 单独叫 DQL；本笔记跟课把查询归在 DML。TCL 细节在后文事务章再展开。

**一条业务线串起来（案例）**

要上线一个课程库，通常顺序是：

```text
1) DCL：CREATE USER + GRANT
   → 给应用账号权限，不用 root 跑业务

2) DDL：CREATE DATABASE / CREATE TABLE
   → 建出 course 表（只有空架子，还没有课程行）

3) DML：INSERT / SELECT / UPDATE / DELETE
   → 往表里插课程、查列表、改标题、删下架课

4) TCL：COMMIT / ROLLBACK
   → 例如「扣库存 + 写订单」两步，要么都成功，要么都回滚
```

**容易混的一点**

| 你想做的事 | 改的是什么 | 类别 |
|------------|------------|------|
| 给表加一列 email | 表结构 | DDL（ALTER） |
| 给某用户填上邮箱 | 某一行的值 | DML（UPDATE） |
| 删掉整张表 | 表结构（数据也没了） | DDL（DROP） |
| 删掉表里几行 | 行数据（表还在） | DML（DELETE） |

日常写业务代码：**DML 最多**，其次是迁移脚本里的 **DDL**；DCL 多在运维/初始化；TCL 在要保证一致性的写操作里用。

### 3.1 练习 ★★★★★

1. SQL 全称是什么？和 Java 最大差别是什么？  
2. DCL / DDL / DML / TCL 各管什么？各举一句。  
3. `CREATE TABLE course (...)` 和 `SELECT * FROM course` 分别属于哪类？为什么？  
4. 「给表加一列」和「给某行填邮箱」为什么不是同一类？  
5. 后端日常哪两类用得最多？TCL 什么时候才需要想？  

**答案**

1. Structured Query Language；SQL 描述「要什么结果」，Java 写「怎么一步步算」。  
2. DCL 管权限如 `GRANT`；DDL 管结构如 `CREATE TABLE`；DML 管数据如 `INSERT`；TCL 管事务如 `COMMIT`。  
3. 前者 DDL（在建表结构）；后者 DML（在读表里的行）。  
4. 加列改的是表结构（DDL）；填邮箱改的是某一行的值（DML）。  
5. DML 最多，其次 DDL；多步写操作必须一起成功或一起失败时才用 TCL。  

### 3.2 DCL ★★★★

DCL（Data Control Language）管**谁能访问**库/表。生产环境不要用 root 跑业务。正确顺序：创建账号 → 授权 → 收回多余权限。

| 语句 | 作用 |
|------|------|
| CREATE USER | 创建账号并设置密码 |
| GRANT | 授予权限 |
| REVOKE | 收回权限 |

用户身份是 `用户名@主机`。主机不同就是不同账号。`%` 表示任意主机，面太大，生产慎用。

```sql
CREATE USER 'app_user'@'192.168.1.%' IDENTIFIED BY '强密码';
GRANT SELECT, INSERT, UPDATE, DELETE ON app_db.* TO 'app_user'@'192.168.1.%';
REVOKE INSERT, UPDATE, DELETE ON app_db.* FROM 'app_user'@'192.168.1.%';
```

**易错点：** MySQL 8.0 起必须先 CREATE USER 再 GRANT；授权者只能授予自己已有的权限；只读账号不要给写权限。

### 3.2 练习 ★★★★

1. DCL 三条核心语句是什么？  
2. 为什么生产不用 root 跑业务？  
3. `user@'192.168.1.%'` 和 `user@'%'` 有什么差别？  
4. 8.0 上只写 GRANT、不先建用户会怎样？  
5. 只读报表账号应该怎么授权？  

**答案**

1. CREATE USER、GRANT、REVOKE。  
2. root 权限过大，泄露或误操作风险高。  
3. 前者限制网段；后者允许任意主机。  
4. 会报错，因为 8.0 起 GRANT 不再自动建用户。  
5. 只授予 SELECT。  

### 3.3 DDL ★★★★★

DDL（Data Definition Language）管**库/表的结构**：建、改、删对象。改的是架子，不是某一行的值。

#### CREATE（建库 / 建表）

```sql
CREATE DATABASE app_db DEFAULT CHARACTER SET utf8mb4;
USE app_db;

CREATE TABLE user (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

| 部分 | 含义 |
|------|------|
| 列定义 | 列名、类型、是否为空、默认值、注释 |
| 表级约束 | 主键、唯一键等 |
| 表选项 | 引擎、字符集、表注释 |

常用列约束：`NOT NULL`、`DEFAULT`、`AUTO_INCREMENT`、`COMMENT`。  
临时表用 `CREATE TEMPORARY TABLE`，只在当前会话有效，断开即删。  
`ALTER DATABASE` 一般只能改字符集等，**不能改库名**。`DROP DATABASE` 会删掉库内全部对象和数据。

#### ALTER / DROP（改结构 / 删对象）

表已存在后用 ALTER 改结构；确认不再需要时才 DROP。

| 操作 | 语句要点 |
|------|----------|
| 加列 | `ALTER TABLE t ADD 列名 类型 …`，可用 AFTER 指定位置 |
| 改定义、不改名 | `MODIFY 列名 新定义` |
| 改名并可改定义 | `CHANGE 旧列名 新列名 新定义` |
| 删列 | `DROP COLUMN 列名`（该列数据一起删除，先备份） |
| 删表 | `DROP TABLE t1, t2`（结构+数据全删） |
| 清空表数据 | `TRUNCATE TABLE t`（保留表结构，属于 DDL） |
| 改表名 | `RENAME TABLE a TO b`（数据还在） |

```sql
ALTER TABLE user ADD email VARCHAR(128) NULL AFTER username;
ALTER TABLE user MODIFY email VARCHAR(256) NOT NULL;
ALTER TABLE user DROP COLUMN email;
```

TRUNCATE 通常比 DELETE 全表更快，但一般不能靠二进制日志做逐行恢复。生产改表优先用迁移脚本，并先备份。

**案例：DDL vs DML 别混**

| 你想做的事 | 语句类型 | 例子 |
|------------|----------|------|
| 给表加一列 email | DDL | `ALTER TABLE … ADD email …` |
| 给某用户填上邮箱 | DML | `UPDATE user SET email=…` |
| 删掉整张表 | DDL | `DROP TABLE user` |
| 删掉表里几行 | DML | `DELETE FROM user WHERE …` |

### 3.3 练习 ★★★★★

1. CREATE DATABASE 和 CREATE TABLE 各做什么？业务库为何常指定 utf8mb4？  
2. PRIMARY KEY、NOT NULL、ENGINE 各表示什么？临时表和普通表差别？  
3. ADD、MODIFY、CHANGE 分别做什么？  
4. DROP COLUMN、DROP TABLE、TRUNCATE 各删什么？最要注意什么？  
5. 「加一列」和「给某行填邮箱」为什么不是同一类语句？  

**答案**

1. 建库；在库中建表结构。utf8mb4 更好支持多字节和 emoji。  
2. 主键；不允许空；指定存储引擎。临时表只在当前会话存在，断开即删。  
3. 加列；改列定义但不改名；改名且可同时改定义。  
4. DROP COLUMN 删列及其中数据；DROP TABLE 删整张表；TRUNCATE 清空数据但留表结构。删前必须备份。  
5. 加列改的是表结构（DDL）；填邮箱改的是某一行的值（DML）。  

### 3.4 DML ★★★★★

DML（Data Manipulation Language）管**表里的行数据**（内容），不是表结构。  
常用四条：`INSERT` / `DELETE` / `UPDATE` / `SELECT`。日常业务代码里 DML 用得最多。

> 对照：DDL 改架子（加列、建表）；DML 改/查内容（填邮箱、查列表）。WHERE 是 SELECT（以及 UPDATE/DELETE）里的过滤子句，同属 DML 这一块。

#### INSERT

**INSERT 是什么**

往表里**新增一行（或多行）**。表结构（有哪些列）已经存在；INSERT 只往里面塞数据。

假设分类表 `imc_class` 长这样：

| 列 | 说明 |
|----|------|
| `class_id` | 主键，自增，不用手写 |
| `class_name` | 分类名，业务上要求唯一 |
| `add_time` | 有默认值（当前时间），可不写 |

**语法拆开看**

```sql
INSERT INTO 表名 (列1, 列2, …)
VALUES (值1, 值2, …);
```

| 部分 | 含义 |
|------|------|
| `INSERT INTO 表名` | 往哪张表插 |
| `(列1, 列2)` | 这次要填哪些列（顺序自定，但要和值对齐） |
| `VALUES (值1, 值2)` | 各列对应填什么；**几个列就几个值，位置一一对应** |

**编写三步（照这个想就不会乱）**

```text
1) 插到哪张表？     → INSERT INTO imc_class
2) 哪些列必须手填？ → 自增、有默认值的通常不写；NOT NULL 且无默认的必须写
3) 值怎么写？       → 和列一一对应；字符串加单引号 '…'
```

**案例：插两条分类**

```sql
INSERT INTO imc_class (class_name)
VALUES ('MySQL'), ('Redis');
```

插入后表里会多出类似：

| class_id | class_name | add_time |
|----------|------------|----------|
| 1 | MySQL | （自动） |
| 2 | Redis | （自动） |

`class_id`、`add_time` 没写，数据库按自增/默认值填好。

**为什么要写列名（推荐）**

```sql
-- 推荐：列和值对着，表以后加列也不容易插错位
INSERT INTO imc_class (class_name) VALUES ('MongoDB');

-- 不推荐：省略列名 = 按表当前列顺序塞满；多一列/少一列就错
INSERT INTO imc_class VALUES (NULL, 'MongoDB', NOW());
```

**唯一冲突（课上重点）**

`class_name` 若建了 UNIQUE，再插一次 `'MySQL'`：

| 情况 | 结果 |
|------|------|
| 无 UNIQUE | 两条 `'MySQL'` 都能进 → 脏数据 |
| 有 UNIQUE，普通 INSERT | 失败，常见错误号 **1062**，表数据不变 |
| 冲突时想改成「更新某列」 | 加 `ON DUPLICATE KEY UPDATE` |

```sql
INSERT INTO imc_class (class_name) VALUES ('MySQL')
  ON DUPLICATE KEY UPDATE add_time = NOW();
-- 没有同名 → 插入新行
-- 已有同名（撞唯一键）→ 不插新行，只把该行的 add_time 改成现在
```

#### SELECT 基础

SELECT 用来取出**结果集**；用得最多也最复杂。UPDATE / DELETE 之前也要先找到目标行。

```text
SELECT  要显示的列或表达式
FROM    从哪张表取         -- 从表查时需要；只算常量时可省略
;
```

逻辑：先定「看哪些列」→ 再定「从哪张表」→ 返回结果集。

```sql
-- 不写 FROM：只输出常量/算式
SELECT 'hello', 2018 + 1;   -- hello | 2019

-- 从表取指定列（推荐写列名，少用 *）
SELECT class_id, class_name FROM imc_class;
SELECT class_id, class_name FROM imc_db.imc_class;  -- 带库名
```

| 写法 | 问题 / 做法 |
|------|-------------|
| `SELECT *` | 多余列耗 IO；TEXT 等大字段更慢；表结构一变结果列也变 |
| `SELECT a, b` | 推荐：意图清晰、更稳 |

FROM 可以是物理表、视图或子查询衍生表（后文再讲）。

#### WHERE（比较过滤）

WHERE **过滤行**：SELECT 管列，WHERE 管行。多数业务查询都会带。

```text
1) 数据在哪张表？  → FROM
2) 要看哪些列？    → SELECT（少用 *）
3) 留下哪些行？    → WHERE
```

```sql
SELECT title, study_cnt
FROM imc_course
WHERE study_cnt > 1000;
```

| 运算符 | 含义 |
|--------|------|
| `=` | 等于 |
| `<>` / `!=` | 不等于 |
| `>` / `>=` / `<` / `<=` | 大小比较 |
| `BETWEEN a AND b` | 区间内，**含两端** |
| `LIKE` | 模糊：`%`任意多个字符，`_`恰好一个 |
| `IS NULL` / `IS NOT NULL` | 判空（不能写 `= NULL`） |

```sql
SELECT title, study_cnt FROM imc_course WHERE study_cnt = 1000;
SELECT title, study_cnt FROM imc_course
WHERE study_cnt BETWEEN 1000 AND 2000;
SELECT title FROM imc_course WHERE title LIKE '%mysql%';

-- 现象：= NULL 永远筛不出 → 改法 IS NULL
SELECT * FROM test_null WHERE c1 = NULL;      -- 错
SELECT * FROM test_null WHERE c1 IS NULL;     -- 对
```

#### 逻辑运算符（多条件）

一个比较条件不够时，用逻辑运算符把多个条件拼进同一个 WHERE。

| 运算符 | 另一种写法 | 何时为真 | 白话 |
|--------|------------|----------|------|
| `AND` | `&&` | 两边都真 | 同时满足 |
| `OR` | `\|\|` | 至少一边真 | 满足任一 |
| `XOR` | （无常用符号） | 恰好一边真 | 二选一，不能两个都真 |

**案例：同一张课表两个条件**

需求 A：标题含 `mysql` **并且** 学习人数 > 5000 → 用 `AND`

```sql
SELECT title, study_cnt
FROM imc_course
WHERE title LIKE '%mysql%'
  AND study_cnt > 5000;
```

需求 B：标题含 `mysql` **或者** 学习人数 > 5000 → 用 `OR`  
（标题匹配但人数不够的会留下；人数够但标题不含的也会留下）

```sql
SELECT title, study_cnt
FROM imc_course
WHERE title LIKE '%mysql%'
   OR study_cnt > 5000;
```

需求 C：两个条件「一真一假」才要（XOR）——少用，面试可能问。

```sql
SELECT title, study_cnt
FROM imc_course
WHERE (title LIKE '%mysql%') XOR (study_cnt > 5000);
-- 等价理解：含 mysql 但人数≤5000，或不含 mysql 但人数>5000
```

**易错点**

1. `AND` / `OR` 搞反：要「同时」用 AND，要「任一」用 OR。  
2. 混用时加括号，避免优先级搞错：

```sql
-- 推荐写清楚
WHERE status = 1 AND (title LIKE '%mysql%' OR study_cnt > 5000)
```

3. 参与运算的列若可能是 `NULL`，逻辑结果可能变成未知，筛不到预期行 → 建表时常把业务列设 `NOT NULL`。

#### 多表查询 · 内连接

**为什么要多表查**

需要的字段不在一张表里时，不能只 `FROM` 一张表。  
前提：表之间有关联——常见是子表存父表主键，这列叫**外键**（逻辑上用来挂关系；课上先会写 JOIN 即可）。

```text
课程 imc_course          章节 imc_chapter
course_id (主键)   ←——    course_id (外键，指向课程)
title                     title（章节名）
```

一门课多个章节 → 用 `chapter.course_id = course.course_id` 把两表接上。

**内连接（INNER JOIN）是什么**

只保留**两边都能按关联条件匹配上的行**（两表交集）。  
只在课程表、章节表没有对应行 → 这门课在内连接结果里**不会出现**。

```sql
SELECT a.course_id, a.title AS course_title, b.title AS chapter_title
FROM imc_course AS a
INNER JOIN imc_chapter AS b
  ON b.course_id = a.course_id;
```

| 部分 | 含义 |
|------|------|
| `FROM 表A JOIN 表B` | 参与关联的两张表（`INNER` 可省略，习惯写上更清晰） |
| `ON 条件` | 怎么对齐，通常是外键 = 主键 |
| `AS a` / `AS b` | 表别名；列写成 `a.title`，避免两表同名列冲突 |

**案例（违规现象 → 理解内连接）**

1. 先查有章节的课 → 内连接能出课程名 + 章节名。  
2. 往课程表插入「MySQL关联测试」，**还不建章节**。  
3. 再内连接并 `WHERE a.title = 'MySQL关联测试'` → **结果为空**。  

原因：内连接要两边都有匹配；新课在章节表没有行，被丢掉。  
若仍要列出「没有章节的课」→ 用外连接。

**写法逻辑**

```text
1) 字段分别在哪几张表？ → 确定 FROM / JOIN
2) 用哪一列对齐？       → 写 ON（外键 = 主键）
3) 要哪些列？           → SELECT，带表别名
4) 还要滤行？           → WHERE（写在 JOIN 之后）
```

> 别名回忆：`FROM imc_course AS a` = 表简称 a；`b.title AS chapter_title` = 结果列显示名。

#### 多表查询 · 外连接

**外连接是什么**

内连接丢掉「只有一边」的行；外连接可以**保留某一边的全部行**。  
最常用：**LEFT JOIN（左外连接）**——保留左表全部行；右表匹配不上时，右表列填 `NULL`。

```text
LEFT JOIN：左表每行都在；右表有匹配就贴上，没有则右表列 = NULL
RIGHT JOIN：反过来保右表（少用；把表左右对调写成 LEFT JOIN 即可）
```

**案例：没章节的课也要列出来**

内连接查「MySQL关联测试」→ 空。改成左连接（课程在左、章节在右）：

```sql
SELECT a.course_id, a.title AS course_title, b.title AS chapter_title
FROM imc_course AS a
LEFT JOIN imc_chapter AS b
  ON b.course_id = a.course_id
WHERE a.title = 'MySQL关联测试';
```

| course_id | course_title | chapter_title |
|-----------|--------------|---------------|
| （有） | MySQL关联测试 | `NULL`（章节表没有对应行） |

逻辑：左表课程行保留；右表对不上 → 章节名显示为空。

**进阶：只要「没有章节」的课**

在左连接结果上，过滤右表关键列为 `NULL`：

```sql
SELECT a.course_id, a.title
FROM imc_course AS a
LEFT JOIN imc_chapter AS b
  ON b.course_id = a.course_id
WHERE b.course_id IS NULL;
```

| 写法 | 含义 |
|------|------|
| `LEFT JOIN` + 无额外过滤 | 有章节的课 + 没章节的课都在（没章节时章节列为 NULL） |
| `LEFT JOIN` + `WHERE b.xxx IS NULL` | 只要左有、右没有的行（反查缺失数据） |

> 补：也可用 `WHERE a.course_id NOT IN (SELECT course_id FROM imc_chapter)`，但关联写法通常更清晰；注意 `NOT IN` 子查询里若有 NULL 会踩坑。

#### GROUP BY 与聚合函数

**是什么**

`GROUP BY`：按某些列的值把结果集**分成若干组**，每组通常再算一个统计值。  
聚合函数：对一组行算出一个数（个数、总和、平均等）。二者经常一起用。

**常用聚合函数**

| 函数 | 作用 |
|------|------|
| `COUNT(*)` | 统计行数 |
| `COUNT(列)` | 统计该列非 NULL 的行数 |
| `COUNT(DISTINCT 列)` | 统计去重后的个数 |
| `SUM(列)` | 求和（数值列） |
| `AVG(列)` | 平均值 |
| `MAX(列)` / `MIN(列)` | 最大 / 最小 |

不写 `GROUP BY` 时，整张表（或过滤后的全部行）当作**一组**，也能用聚合：

```sql
SELECT COUNT(*) AS course_cnt FROM imc_course;           -- 总课程数
SELECT SUM(study_cnt) AS total_learners FROM imc_course; -- 学习人次合计
SELECT COUNT(DISTINCT teacher_id) FROM imc_course;       -- 有多少不同讲师
```

**按组统计（GROUP BY）**

需求：每种难度有多少门课。

```sql
SELECT l.level_name, COUNT(*) AS course_cnt
FROM imc_course AS c
INNER JOIN imc_level AS l ON l.level_id = c.level_id
GROUP BY l.level_name;
```

逻辑：先 JOIN 得到明细 → 再按 `level_name` 分组 → 每组 `COUNT(*)` 一门数。  
`GROUP BY` 后的列叫**分组键**；每个分组键取值在结果里通常只占一行。

**硬规则（必记）**

`SELECT` 里出现的**非聚合列**，必须出现在 `GROUP BY` 中。

| 现象 | 原因 → 改法 |
|------|-------------|
| 只 `GROUP BY level_name`，却 `SELECT class_name, level_name, COUNT(*)` | 宽松模式下可能跑出，但分类名残缺/不准；严格模式（`ONLY_FULL_GROUP_BY`）直接报错 |
| 正确 | 两个维度都要统计时：`GROUP BY class_name, level_name` |

```sql
-- 每个分类 × 每种难度 的课程数
SELECT b.class_name, c.level_name, COUNT(*) AS course_cnt
FROM imc_course AS a
INNER JOIN imc_class AS b ON b.class_id = a.class_id
INNER JOIN imc_level AS c ON c.level_id = a.level_id
GROUP BY b.class_name, c.level_name;
```

#### HAVING（过滤分组结果）

**是什么**

`HAVING`：对 **GROUP BY 聚合之后** 的结果再过滤。  
`WHERE`：对 **分组之前** 的行过滤。

| | WHERE | HAVING |
|--|-------|--------|
| 时机 | 先滤行，再分组 | 先分组聚合，再滤组 |
| 聚合函数 | **不能**写（如 `COUNT(*)>3` 会报错） | **可以**写 |
| 典型用途 | `status=1`、`study_cnt>1000` | `COUNT(*)>3`、`SUM(x)>100` |

**案例（现象 → 改法）**

需求：每个分类下，某难度的课程门数 **大于 3** 的那些组合。

```sql
-- 错：WHERE 里用聚合 → 报错（非法使用聚合函数）
SELECT b.class_name, c.level_name, COUNT(*) AS course_cnt
FROM imc_course AS a
INNER JOIN imc_class AS b ON b.class_id = a.class_id
INNER JOIN imc_level AS c ON c.level_id = a.level_id
WHERE COUNT(*) > 3
GROUP BY b.class_name, c.level_name;

-- 对：聚合条件放 HAVING
SELECT b.class_name, c.level_name, COUNT(*) AS course_cnt
FROM imc_course AS a
INNER JOIN imc_class AS b ON b.class_id = a.class_id
INNER JOIN imc_level AS c ON c.level_id = a.level_id
GROUP BY b.class_name, c.level_name
HAVING COUNT(*) > 3;
```

逻辑：先按分类+难度分组并 `COUNT` → 再丢掉门数 ≤3 的组 → 只留门数 >3 的行。

**执行顺序（方便记）**

```text
FROM / JOIN → WHERE（滤行）→ GROUP BY（分组）→ 聚合 → HAVING（滤组）→ SELECT → ORDER BY
```

行条件用 WHERE；组条件用 HAVING。

#### ORDER BY / LIMIT

**ORDER BY：结果排序**

不写 ORDER BY 时，结果顺序**不保证**（InnoDB 常看起来像按主键，但不能依赖）。  
需要稳定顺序时，必须写 `ORDER BY`。

```sql
SELECT title, study_cnt
FROM imc_course
ORDER BY study_cnt DESC;   -- 降序：从高到低
-- ORDER BY study_cnt ASC; -- 升序：从低到高（ASC 可省略，默认升序）
```

| 要点 | 说明 |
|------|------|
| `ASC` / `DESC` | 升序 / 降序，写在排序列后面 |
| 多列排序 | `ORDER BY a DESC, b ASC`：先按 a，a 相同再按 b |
| 可排未选出的列 | 与 GROUP BY 不同，ORDER BY 可以用 SELECT 里没出现的列 |

**LIMIT：限制返回行数（分页）**

结果集很大时，一次全返回会压客户端内存和网络。分页用 LIMIT。

```sql
-- LIMIT 起始位置, 返回行数   （起始从 0 开始）
SELECT course_id, title
FROM imc_course
ORDER BY study_cnt DESC
LIMIT 0, 10;    -- 第 1 页：跳过 0 行，取 10 行

LIMIT 10, 10;   -- 第 2 页：跳过 10 行，取 10 行
LIMIT 20, 10;   -- 第 3 页
```

也可写成 `LIMIT 10 OFFSET 20`（先写条数再写偏移，注意和上面两种参数顺序不同）。

**硬规则：LIMIT 要配合 ORDER BY**

分页每次都会重新查；没有 ORDER BY，各页顺序可能乱，同一行可能出现在不同页或漏页。  
正确写法：**先 ORDER BY，再 LIMIT**。

```text
… WHERE … GROUP BY … HAVING … ORDER BY … LIMIT …
```

#### DELETE / UPDATE

二者都改**已有行**；写之前先用同一条件的 `SELECT` 确认目标行，再改成 DELETE / UPDATE。

##### DELETE（删行）

删的是行数据，表结构还在。全表清空更推荐 `TRUNCATE`（DDL）；只删部分行用 `DELETE` + `WHERE`。

```sql
DELETE FROM 表名
WHERE 条件;
```

**案例：删除没有章节的课程**（先查出，再删）

```sql
-- 1) 先查确认
SELECT a.course_id, a.title
FROM imc_course AS a
LEFT JOIN imc_chapter AS b ON b.course_id = a.course_id
WHERE b.course_id IS NULL;

-- 2) 改成删除（多表时写明删哪张表）
DELETE a
FROM imc_course AS a
LEFT JOIN imc_chapter AS b ON b.course_id = a.course_id
WHERE b.course_id IS NULL;
```

| 要点 | 说明 |
|------|------|
| 必写 WHERE（多数情况） | 忘写 = 可能删光整表 |
| 多表 | `DELETE 别名 FROM … JOIN … WHERE …` |
| LIMIT | 可限删除行数；只有一个参数（最大删几行） |
| vs TRUNCATE | 全表清空优先 TRUNCATE；按条件删用 DELETE |

> 补：删重复行常见套路——`GROUP BY … HAVING COUNT(*)>1` 找出重复，保留 `MIN(id)`，删掉其余，再加 UNIQUE。

##### UPDATE（改列值）

```sql
UPDATE 表名
SET 列1 = 值1, 列2 = 值2
WHERE 条件;
```

**案例：冻结用户**

```sql
-- 1) 先查
SELECT user_nickname, user_status FROM imc_user WHERE user_nickname = '沙站';

-- 2) 再改（status: 1 正常 → 0 禁用）
UPDATE imc_user
SET user_status = 0
WHERE user_nickname = '沙站';
```

| 要点 | 说明 |
|------|------|
| SET | 要改的列 = 新值；多列用逗号分隔 |
| WHERE | **极重要**；写错/漏写可能改全表 |
| 安全流程 | 同条件先 SELECT → 确认 → 再 UPDATE |
| ORDER BY + LIMIT | 少用于日常；大批量可分批更新，减轻锁与延迟 |

### 3.4 练习 ★★★★★

1. DELETE 和 TRUNCATE 差别？UPDATE 的 SET / WHERE 各做什么？  
2. 为什么 DELETE / UPDATE 前要先写 SELECT？  
3. 漏写 WHERE 会怎样？  
4. 多表 DELETE 时为什么要写 `DELETE a`？  
5. 写出：把 `course` 中 `id=10` 的 `title` 改成 `'新标题'`。  

**答案**

1. DELETE 按条件删行；TRUNCATE 清空整表属 DDL。SET 指定改哪些列和值；WHERE 限定改哪些行。  
2. 确认将要动到的行是否正确，避免误删误改。  
3. 可能删光或改光整表。  
4. JOIN 多表时要指明删的是哪张表的行。  
5. `UPDATE course SET title = '新标题' WHERE id = 10;`  

### 3.5 常用函数 ★★★★

#### 时间

业务里录入/修改时间应尽量用**数据库服务器时间**，避免多台应用服务器时钟不一致。  
时间要用日期时间类型存，**不要存成字符串**（难校验、难运算、更占空间）。需要某种显示格式时用函数格式化输出即可。

#### 取当前时间

| 函数 | 返回 |
|------|------|
| `CURRENT_DATE` / `CURDATE()` | 当前日期 |
| `CURRENT_TIME` / `CURTIME()` | 当前时间（时分秒） |
| `NOW()` | 当前日期 + 时间 |

```sql
SELECT CURRENT_DATE, CURRENT_TIME, NOW();
```

#### 格式化输出

`DATE_FORMAT(时间, 格式)`：按指定格式输出（存的仍是时间类型）。

| 格式符 | 含义 |
|--------|------|
| `%Y` | 四位年 |
| `%m` | 月（01–12） |
| `%d` | 日（01–31） |
| `%H` | 时（00–23） |
| `%i` | 分 |
| `%s` | 秒 |

```sql
SELECT DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s');
```

#### 换算与日差

| 函数 | 作用 |
|------|------|
| `SEC_TO_TIME(秒)` | 秒 → `HH:MM:SS` |
| `TIME_TO_SEC('HH:MM:SS')` | 时分秒 → 秒 |
| `DATEDIFF(日期1, 日期2)` | 两个日期相差天数 |

```sql
SELECT SEC_TO_TIME(60);              -- 00:01:00
SELECT TIME_TO_SEC('01:00:00');      -- 3600
SELECT title, DATEDIFF(NOW(), online_at) AS days_online
FROM imc_course
ORDER BY days_online DESC;
```

#### 加减与提取

```sql
-- DATE_ADD：按时间单元加减（减用负数）
SELECT NOW() AS now,
       DATE_ADD(NOW(), INTERVAL 1 DAY) AS plus_1d,
       DATE_ADD(NOW(), INTERVAL 1 YEAR) AS plus_1y,
       DATE_ADD(NOW(), INTERVAL -1 DAY) AS minus_1d,
       DATE_SUB(NOW(), INTERVAL 90 MINUTE) AS minus_1h30m;
-- 时间单元常见：YEAR / MONTH / DAY / HOUR / MINUTE / SECOND

-- EXTRACT：取时间的某一部分
SELECT EXTRACT(YEAR FROM NOW()),
       EXTRACT(MONTH FROM NOW()),
       EXTRACT(DAY FROM NOW());
```

> 补：`UNIX_TIMESTAMP` / `FROM_UNIXTIME` 可做时间戳互转；新项目更推荐直接用日期时间类型，少用纯时间戳存库。

#### 字符串

**拼接**

| 函数 | 作用 |
|------|------|
| `CONCAT(a, b, …)` | 直接拼接多个字符串/列 |
| `CONCAT_WS(分隔符, a, b, …)` | 用指定分隔符拼接 |

```sql
SELECT CONCAT(b.class_name, a.title) AS raw_join
FROM imc_course a
INNER JOIN imc_class b ON b.class_id = a.class_id;

SELECT CONCAT_WS('||', b.class_name, a.title) AS seo_title
FROM imc_course a
INNER JOIN imc_class b ON b.class_id = a.class_id;
```

**长度**

| 函数 | 含义 |
|------|------|
| `CHAR_LENGTH(s)` / `CHARACTER_LENGTH(s)` | **字符**个数 |
| `LENGTH(s)` | **字节**个数 |

utf8mb4 下中文常是多字节：`"安全测试"` → `CHAR_LENGTH=4`，`LENGTH` 更大。英文字母两者常相同。

**截取**

| 函数 | 作用 |
|------|------|
| `LEFT(s, n)` / `RIGHT(s, n)` | 从左/右取 n 个字符 |
| `SUBSTRING(s, pos)` | 从第 pos 个字符到末尾（下标从 1 起） |
| `SUBSTRING(s, pos, len)` | 从 pos 起取 len 个 |
| `SUBSTRING_INDEX(s, 分隔符, n)` | 按分隔符切；`n` 正=从左第 n 个分隔符之前，负=从右 |

```sql
SELECT LEFT('www.imc.com', 3), RIGHT('www.imc.com', 3);  -- www / com
SELECT SUBSTRING('www.imc.com', 5);                      -- imc.com
SELECT SUBSTRING_INDEX('192.168.0.100', '.', -2);        -- 0.100
```

**定位 + 截取（案例）**

标题形如 `前缀-后缀`，只要横线前：

```sql
-- LOCATE 找位置（从 1 起）；截到横线前要减 1
SELECT title,
       SUBSTRING(title, 1, LOCATE('-', title) - 1) AS prefix
FROM imc_course
WHERE LOCATE('-', title) > 0;
```

**数字千分位显示**

`FORMAT(数值, 小数位数)`：整数部分每三位逗号，并四舍五入到指定小数位。

```sql
SELECT FORMAT(123456.789, 2);  -- 123,456.79
```

#### 其它常用

| 函数 | 作用 | 注意 |
|------|------|------|
| `ROUND(x, d)` | 四舍五入到 d 位小数 | 金额计算常用 |
| `RAND()` | 0～1 随机数 | 大表 `ORDER BY RAND()` 很慢，不推荐 |
| `CASE WHEN … THEN … ELSE … END` | 按条件换显示/逻辑 | 可出现在 SELECT / WHERE |
| `MD5(s)` | 算 MD5 | 课上用于密码示例；现代系统密码应用更强哈希（工作里别只靠 MD5） |

```sql
SELECT user_nick,
       CASE
         WHEN sex = 1 THEN '男'
         WHEN sex = 0 THEN '女'
         ELSE '未知'
       END AS gender
FROM imc_user;
```

### 3.5 练习 ★★★★

1. 为何业务时间用库时间？为何时间不要存字符串？  
2. `CONCAT` 和 `CONCAT_WS` 差别？`LENGTH` 和 `CHAR_LENGTH` 差别？  
3. `LEFT` / `SUBSTRING` / `SUBSTRING_INDEX` 各做什么？  
4. 如何截取 `title` 里第一个 `-` 之前的部分？  
5. `CASE WHEN` 和 `ORDER BY RAND()` 各注意什么？  

**答案**

1. 多机时钟可能不一致；字符串难校验/运算，应用日期时间类型 + `DATE_FORMAT` 输出。  
2. 后者可指定分隔符；`LENGTH` 计字节，`CHAR_LENGTH` 计字符。  
3. 左右取固定长度；从位置截取；按分隔符取段。  
4. `SUBSTRING(title, 1, LOCATE('-', title) - 1)`（先确认有 `-`）。  
5. CASE 把编码转成可读文案等；大表不要用 `ORDER BY RAND()` 做随机，性能差。

---

## 第4章 索引

### 4.1 索引是什么 · B+Tree ★★★★★

**是什么**

索引告诉存储引擎**怎么快速找到**需要的行。它在**存储引擎层**实现（不是 SQL 服务层），所以不同引擎的索引细节可能不同。  
日常说「索引」，默认指 **B+Tree 索引**（InnoDB 最常用）。

| 类型 | 要不要管 |
|------|----------|
| B+Tree | **必学**，默认就是它 |
| 自适应哈希 | InnoDB 自动建，开发不用管 |
| 全文 / 空间 | 入门可跳；中文全文更常上专门搜索引擎 |

**B+Tree 要点（能口述）**

1. 平衡多路树：根到任意叶子的高度一样。  
2. **记录在叶子层**，按键值有序。  
3. 叶子之间有指针相连 → 方便范围扫描。  
4. InnoDB：**聚簇索引**按主键存整行；**二级索引**叶子存的是主键，再回表取整行。

**适合什么样的条件**

| 场景 | 例子 | 能否用 B+Tree |
|------|------|----------------|
| 全值匹配 | `class_name = 'MySQL'`、`IN ('a','b')` | 能（`IN` 值过多时优化器可能改走全表） |
| 范围 | `BETWEEN`、`>`、`<` | 能（键有序） |
| 复合索引 | `(title, study_cnt)` | 必须从**最左列**开始匹配 |

复合索引最左前缀（案例）：索引 `(title, study_cnt)`

| 条件 | 能否用该索引 |
|------|----------------|
| `WHERE title = 'x'` | 能 |
| `WHERE title = 'x' AND study_cnt > 3000` | 能（优化器可调整条件顺序） |
| `WHERE study_cnt > 3000`（没用 title） | **不能**（跳过了最左列） |

**在哪些列建索引**

没有万能答案，结合具体 SQL 和选择性。

1. 优先考虑 `WHERE` / `JOIN ON` / `ORDER BY` 里用到的列。  
2. 要看**选择性**：不同值越多越好（主键最好；性别只有 0/1 很差）。  
3. 课上案例：查「某日后注册的男性」→ `reg_time` 选择性远好于 `sex`；只在 `sex` 上建索引，执行计划仍可能全表扫描。

```sql
-- 选择性粗算：唯一值个数 / 总行数（越大越好）
SELECT
  COUNT(DISTINCT sex) / COUNT(*) AS sex_sel,
  COUNT(DISTINCT DATE(reg_time)) / COUNT(*) AS reg_sel
FROM imc_user;

CREATE INDEX idx_reg_time ON imc_user (reg_time);
```

> 补：索引加速读，但会增加写成本、占空间；不是列越多越好。误区与 EXPLAIN 下一小节展开。

### 4.1 练习 ★★★★★

1. 索引主要解决什么问题？为什么说在引擎层？  
2. B+Tree 三个特点？InnoDB 二级索引叶子存什么？  
3. `IN` 查询一定不能用索引吗？  
4. 复合索引 `(a,b)` 时，只写 `WHERE b=?` 为何常常用不上？  
5. 什么是选择性？性别列和注册时间列谁更适合建索引？  

**答案**

1. 让引擎快速定位行；索引由存储引擎实现，不同引擎细节不同。  
2. 平衡、数据在叶子且有序、叶子相连；二级索引叶子存主键（再回表）。  
3. 不是；值过多时优化器可能不用索引，但不是绝对不能。  
4. 违反最左前缀，必须从 a 开始匹配。  
5. 不同值占比；注册时间通常远好于性别。  
