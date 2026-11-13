# Java 提效

> 结构：要点（含逻辑）→ 案例 → 练习。去掉口播，保留能复习的推理与例子。

## 第1章 函数式编程入门

### 1.1 行为参数化 ★★★★★

**是什么**

把「怎么判断 / 怎么处理」也当成参数传进方法，而不是把规则写死在方法里。  
最终常用 **Lambda** 把一小段行为直接传进去。

**为什么**

需求会变：先只要电子产品 → 再按类型 → 再按价格/多属性。  
规则写死在 `if` 里 → 每变一次就改方法或加新方法，爆炸。

**演化（购物车过滤 SKU）**

| 版本 | 做法 | 问题 / 进步 |
|------|------|-------------|
| V1 | `if (类型 == 电子)` 写死 | 只能筛电子类 |
| V2 | 多传一个 `category` 参数 | 类型可变，但仍只能按类型筛 |
| V3 | 再加价格等参数、分支更多 | 多维度时方法签名和 `if` 失控 |
| V4 | 抽 `SkuPredicate#test(Sku)`，过滤方法接收谓词 | **判断策略**与遍历分离（类似策略模式） |
| V5 | 匿名内部类实现谓词 | 少建类，但仍啰嗦 |
| V6 | Lambda 实现谓词 | 同一能力，代码最短 |

**案例：过滤骨架**

```java
// 谓词：对一个 Sku 返回是否留下
interface SkuPredicate {
    boolean test(Sku sku);
}

List<Sku> filter(List<Sku> cart, SkuPredicate predicate) {
    List<Sku> result = new ArrayList<>();
    for (Sku sku : cart) {
        if (predicate.test(sku)) {   // 具体规则由外部传入
            result.add(sku);
        }
    }
    return result;
}

// V5 匿名类
filter(cart, new SkuPredicate() {
    public boolean test(Sku sku) {
        return sku.getPrice() > 1000;
    }
});

// V6 Lambda（下一节细讲语法）
filter(cart, sku -> sku.getPrice() > 1000);
```

逻辑：`filter` 只负责遍历；**谁留下**由传入的 `test` 决定 → 新需求主要改传入的行为，少改框架代码。

### 1.1 练习 ★★★★★

1. 什么是行为参数化？  
2. 硬编码过滤电子类有什么问题？  
3. `SkuPredicate` 解决了什么？和策略模式像在哪？  
4. 匿名类相比独立实现类的好处？还剩什么缺点？  
5. Lambda 在这条演进线里处在哪一步、解决什么？  

**答案**

1. 把判断/处理逻辑作为参数传入方法。  
2. 需求一变就要改方法；无法灵活换条件。  
3. 把「是否选中」抽象成接口；不同实现即不同策略，运行时传入。  
4. 不用单独建类；仍比 Lambda 冗长。  
5. 在匿名类之后；用更短语法传入同一套行为。

### 1.2 Lambda 与函数式接口 ★★★★★

**Lambda 是什么**

把「一小段行为」写成可传递的表达式，用来替代啰嗦的匿名内部类，落实行为参数化。  
Java 8 起支持：函数也可以像数据一样当参数传（在面向对象之外的一种写法）。

**写法（记住箭头 `->`）**

```text
(参数) -> 表达式
(参数) -> { 语句; return ...; }
```

| 特征 | 说明 |
|------|------|
| 参数类型 | 常可省略，靠编译器推断 |
| 单参数 | 圆括号可省略：`x -> x > 0` |
| 无参 | 圆括号不能省：`() -> 1` |
| 单表达式 | 可省大括号，自动 return |
| 多语句 | 必须 `{}`，有返回值时要写 `return` |

```java
() -> System.out.println("hi");
x -> x * 2;
(x, y) -> x + y;
(long x, long y) -> x + y;          // 也可显式写类型
sku -> sku.getPrice() > 1000;
```

**函数式接口**

| 规则 | 说明 |
|------|------|
| 核心 | 接口里**只有一个抽象方法**（可有 default/static） |
| 标注 | `@FunctionalInterface` 可选，帮编译器检查 |
| 用途 | Lambda 必须「对上」这个唯一抽象方法的签名 |

上一节的 `SkuPredicate#test(Sku)` 就是函数式接口；有了它，才能写 `sku -> ...`。

**JDK 常用函数式接口（优先用现成的）**

| 接口 | 抽象方法直觉 | 典型用途 |
|------|--------------|----------|
| `Predicate<T>` | `boolean test(T t)` | 判断、过滤 |
| `Consumer<T>` | `void accept(T t)` | 消费、打印、写入 |
| `Function<T,R>` | `R apply(T t)` | 转换：T→R |
| `Supplier<T>` | `T get()` | 无参产出一个值 |
| `BinaryOperator<T>` | `T apply(T,T)` | 两同类型合成一个（如求和） |

基础类型还有 `IntPredicate` 等，可少装箱。包大致在 `java.util.function`。

```java
Predicate<Sku> expensive = sku -> sku.getPrice() > 1000;
filter(cart, expensive);   // 或直接传入 Lambda
```

**方法引用（`::`）**

Lambda 若只是「调用某个已有方法」，可写成方法引用：`目标::方法名`。

| 形式 | 例子 | 相当于 |
|------|------|--------|
| 静态方法 | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| 某类的实例方法 | `String::length` | `s -> s.length()` |
| 已有对象的实例方法 | `list::add` | `x -> list.add(x)` |

能看懂、会改写即可；不必强行处处用。

### 1.2 练习 ★★★★★

1. Lambda 相对匿名类解决什么问题？  
2. 函数式接口的硬性条件是什么？`@FunctionalInterface` 作用？  
3. `Predicate` / `Consumer` / `Function` 各干什么？  
4. 写出：判断字符串长度 > 5 的 `Predicate<String>`。  
5. `String::toUpperCase` 对应怎样的 Lambda？  

**答案**

1. 用更短语法传递行为，少写样板代码。  
2. 只能有一个抽象方法；注解用于编译期校验。  
3. 判断；消费无返回；转换 T→R。  
4. `s -> s.length() > 5`。  
5. `s -> s.toUpperCase()`。

## 第2章 Stream

### 2.1 Stream 是什么 ★★★★★

**是什么**

Stream（流）是 Java 8 起处理集合数据的方式：用**声明式**流水线（常配合 Lambda）做过滤、映射、排序、收集等。  
不是 IO 流。一句话：从**数据源**生成的元素序列 + 一串数据处理操作。

**和集合的差别**

| | 集合 | Stream |
|--|------|--------|
| 侧重 | **存**数据 | **算**数据 |
| 遍历 | 外部 `for` | **内部迭代**（你描述做什么，框架去遍历） |
| 次数 | 可反复遍历 | **一般只能消费一次**，再用不行 |
| 比喻 | 整张 DVD | 流媒体按需取帧 |

**流水线三部分**

```text
数据源  →  中间操作（可多个，懒执行）  →  终端操作（触发真正计算）
list.stream()   filter / map / sorted…      collect / forEach / count…
```

| 类型 | 特点 | 例子 |
|------|------|------|
| 中间操作 | 返回 Stream，可链式；**懒**的，遇终端才算 | `filter`、`map`、`sorted`、`distinct`、`limit` |
| 终端操作 | 产生结果或副作用，流结束 | `collect`、`forEach`、`count`、`findFirst` |

中间操作还可分：无状态（如 `filter` 看单个元素）/ 有状态（如 `sorted`、`distinct` 常需看更多元素）。  
终端还可分：短路（如 `findFirst` 找到即可停）/ 非短路（如 `forEach` 常扫完）。

**案例：购物车需求用 Stream**

需求：非图书里最贵的两件 → 只要名称，并累加总价（示意）：

```java
List<String> names = cart.stream()
    .filter(sku -> sku.getCategory() != Category.BOOK)  // 中间：过滤
    .sorted(Comparator.comparing(Sku::getTotalPrice).reversed())
    .limit(2)                                           // 中间：截断
    .map(Sku::getName)                                  // 中间：映射
    .collect(Collectors.toList());                      // 终端：收集
```

对比传统写法：多个临时 List + 多次 for；Stream 把步骤串成一条管道，意图更直。

**常见创建**

```java
list.stream();
Arrays.stream(array);
Stream.of(1, 2, 3);
```

> 补：还可从文件、`Stream.iterate` / `generate` 建流；并行 `parallelStream()` 后文再谈。

### 2.1 练习 ★★★★★

1. Stream 和集合各侧重什么？流为何一般只能用一次？  
2. 中间操作和终端操作差别？谁触发真正计算？  
3. 什么叫内部迭代？  
4. 写出：从 `List<Integer>` 过滤出偶数并收集为 List。  
5. `filter` 和 `sorted` 哪个更常是「有状态」操作？为什么？  

**答案**

1. 集合侧重存储，Stream 侧重计算；流按时间维度消费，用过即结束。  
2. 中间返回 Stream 可链式且懒执行；终端出结果并结束流，由终端触发计算。  
3. 不自己写 for，只描述对每个元素做什么，由 Stream 内部遍历。  
4. `list.stream().filter(n -> n % 2 == 0).collect(Collectors.toList());`  
5. `sorted`；通常要基于更多/全部元素才能排好序。

### 2.2 常用中间 / 终端操作 ★★★★★

**中间操作（返回 Stream，可链式；懒执行）**

| 方法 | 作用 | 状态感 |
|------|------|--------|
| `filter(Predicate)` | 留下满足条件的 | 无状态 |
| `map(Function)` | 一对一转换成另一种元素 | 无状态 |
| `flatMap(Function→Stream)` | 一对多，再把多个小流**拍平**成一流 | 无状态 |
| `peek(Consumer)` | 顺手看一眼/打日志，**不结束流**（对比 `forEach`） | 无状态 |
| `sorted()` / `sorted(Comparator)` | 排序 | 有状态 |
| `distinct()` | 去重 | 有状态 |
| `skip(n)` | 跳过前 n 个 | 有状态 |
| `limit(n)` | 只取前 n 个 | 有状态 |

```java
// filter + map
List<String> names = skus.stream()
    .filter(s -> s.getCategory() != Category.BOOK)
    .map(Sku::getName)
    .collect(Collectors.toList());

// flatMap：名称拆成字符流再拍平
skus.stream()
    .flatMap(s -> Arrays.stream(s.getName().split("")))
    .forEach(System.out::print);

// peek 调试（中间）vs forEach（终端，用完流就没了）
skus.stream()
    .peek(s -> System.out.println("see " + s.getName()))
    .filter(s -> s.getTotalPrice() > 100)
    .collect(Collectors.toList());

// 假分页：第 page 页，每页 size 条（page 从 0）
skus.stream()
    .sorted(Comparator.comparing(Sku::getTotalPrice))
    .skip(page * size)
    .limit(size)
    .collect(Collectors.toList());
```

惰性：没终端操作时中间步骤往往还不真正跑；有终端后，元素常「一条条」穿过整条管道（无状态时尤其明显）。

**终端操作（触发计算，流结束）**

| 方法 | 作用 | 短路？ |
|------|------|--------|
| `forEach(Consumer)` | 遍历消费 | 否 |
| `collect(Collector)` | 收集成集合等 | 否 |
| `count()` | 个数 | 否 |
| `max` / `min(Comparator)` | 最大/最小 | 否 |
| `allMatch` / `anyMatch` / `noneMatch` | 全满足 / 有一个满足 / 全不满足 | **是**（可提前停） |
| `findFirst` / `findAny` | 第一个 / 任意一个（并行时 findAny 更松） | **是** |
| `mapToDouble(...).sum/max/...` | 数值特化流上的统计 | 视方法 |

```java
boolean allExpensive = skus.stream()
    .allMatch(s -> s.getTotalPrice() > 100);   // 有一个不满足就停

Optional<Sku> first = skus.stream().findFirst();

double maxPrice = skus.stream()
    .mapToDouble(Sku::getTotalPrice)
    .max()
    .orElse(0);
```

`findFirst` / `max` 等常返回 `Optional`，注意空流。

### 2.2 练习 ★★★★★

1. `map` 和 `flatMap` 差别？  
2. `peek` 和 `forEach` 差别？  
3. `skip` + `limit` 能做什么？  
4. `allMatch` 为什么是短路？  
5. 写出：名称去重后收集为 List。  

**答案**

1. `map` 一对一；`flatMap` 一对多再拍平成一条流。  
2. `peek` 是中间操作，流还可继续；`forEach` 是终端，流结束。  
3. 简易分页（先排序再跳过再截断）。  
4. 发现一个不满足即可返回 false，不必看完。  
5. `list.stream().map(...).distinct().collect(Collectors.toList());`

### 2.3 Collectors 收集器 ★★★★★

**三个易混名字**

| 名字 | 是什么 |
|------|--------|
| `collect(...)` | Stream 的**终端方法**，负责「收尾收集」 |
| `Collector` | 接口：描述「怎么累积成结果」 |
| `Collectors` | 工具类：提供一批现成的 `Collector` |

```java
stream.collect(某个Collector);
```

**预定义收集器三大类（入门够用）**

1. **归约 / 汇总成一个值或集合**：如 `toList`、`toSet`、`counting`、`summingInt`…  
2. **分组** `groupingBy`：按某键分成多组 → `Map<K, List<T>>`  
3. **分区** `partitioningBy`：按布尔谓词分成两组 → `Map<Boolean, List<T>>`（分组的特例）

**案例**

```java
// 1) 收集为 List
List<Sku> expensive = skus.stream()
    .filter(s -> s.getTotalPrice() > 100)
    .collect(Collectors.toList());

// 2) 按类别分组
Map<Category, List<Sku>> byCat = skus.stream()
    .collect(Collectors.groupingBy(Sku::getCategory));
// key=类别，value=该类下的商品列表

// 3) 按总价是否 >100 分区
Map<Boolean, List<Sku>> parts = skus.stream()
    .collect(Collectors.partitioningBy(s -> s.getTotalPrice() > 100));
// true 一组，false 一组
```

逻辑：中间操作负责「算」；`collect` + `Collectors.xxx` 负责「收成什么结构」。  
工作中最熟三剑客：`toList` / `groupingBy` / `partitioningBy`。

> 补：还有 `toMap`、`joining`（拼字符串）、下游收集器（分组后再 `counting`）等，用到再查即可。

### 2.3 练习 ★★★★★

1. `collect`、`Collector`、`Collectors` 各是什么？  
2. `groupingBy` 和 `partitioningBy` 差别？  
3. `partitioningBy` 的 Map 的 key 类型是什么？  
4. 写出：Stream 收集为 `Set`。  
5. 写出：按 `getStatus` 分组。  

**答案**

1. 终端方法；收集器接口；现成收集器工具类。  
2. 按任意键多组 vs 按布尔谓词只分两组。  
3. `Boolean`。  
4. `.collect(Collectors.toSet());`  
5. `.collect(Collectors.groupingBy(Xxx::getStatus));`

## 第3章 资源关闭

### 3.1 try-with-resources ★★★★★

**问题**

文件流、Socket、数据库连接等是**物理资源**：GC 只管堆内存，**不会**可靠地关掉它们。  
用完不关 → 长时间占着连接/句柄 → 别人拿不到 → 系统异常。  
`System.gc()` 也不能保证立刻回收。

**传统写法（JDK7 前）**

`try / finally` 里手动 `close`，还要处理 `close` 自己抛的异常；多资源时「后开先关」，代码又长又易漏。

**try-with-resources（TWR）**

JDK 7+：在 `try (...)` 里声明资源，块结束时**自动关闭**（编译器生成类似 finally + close 的代码）。

```java
try (InputStream in = new FileInputStream(src);
     OutputStream out = new FileOutputStream(dst)) {
    // 读写拷贝…
} // 这里自动 close；多资源按声明的逆序关闭
```

| 要点 | 说明 |
|------|------|
| 资源类型 | 必须实现 `AutoCloseable`（`close()`） |
| 多资源 | `try` 小括号里用 `;` 隔开多个 |
| 关闭顺序 | 后声明的先关（后开先关） |
| 异常 | 关闭时异常会**挂到**原异常上（suppressed），少「把真正原因吃掉」 |

本质是语法糖：方便正确关闭，不是「可以不关」；该关的资源仍要走关闭路径，只是不用手写大段 finally。

**案例对比（逻辑）**

| 写法 | 现象 |
|------|------|
| 打开流从不 close | 功能看似正常，生产易耗尽句柄/连接 |
| finally 手动 close | 正确但冗长，易写错嵌套 |
| try-with-resources | 正确且短，推荐默认写法 |

### 3.1 练习 ★★★★★

1. 为什么流/连接常要手动关，不能只靠 GC？  
2. 不关物理资源会怎样？  
3. try-with-resources 的前提接口是什么？  
4. 多资源关闭顺序？  
5. 写出：用 TWR 打开一个 `FileInputStream` 读（可只写骨架）。  

**答案**

1. GC 主要回收堆内存，不管文件/网络/DB 等物理资源，且时机不可控。  
2. 资源被长期占用，最终可能耗尽导致系统异常。  
3. `AutoCloseable`（有 `close`）。  
4. 后开先关（后声明的先关闭）。  
5. `try (InputStream in = new FileInputStream(path)) { … }`

## 第4章 Guava 工具集

### 4.1 Optional · 不可变集合 · 常用集合工具 ★★★★

**Guava 是什么**

Google 开源的 Java 工具库：集合、缓存、字符串、IO 等日常能力。  
很多想法已被 JDK 吸收（如 `Optional`）；项目里仍常直接用 Guava 的集合与工具类。

#### Optional（优先用 JDK 8）

用「可能有值 / 可能没有」表达缺失，减少乱飞的 `null` 与 NPE。

```java
Optional.empty();                 // 明确没有
Optional.of(x);                   // x 不能为 null，否则立刻 NPE
Optional.ofNullable(x);           // x 可为 null

if (opt.isPresent()) { ... }
opt.orElse(defaultValue);
opt.map(...).orElse(...);
```

逻辑：把「缺值」变成类型的一部分，强迫调用方处理，而不是默默传 null。

#### 不可变集合

创建后不能改；适合当常量、防御性拷贝、多线程共享只读数据。

| 优点 | 说明 |
|------|------|
| 安全 | 不被第三方方法偷偷改掉 |
| 线程友好 | 无写竞争 |
| 可当常量 | 内容固定 |

JDK 的 `Collections.unmodifiableList(list)` 只是包装：原 `list` 一改，视图也变，且啰嗦。  
Guava 更常用：

```java
ImmutableList.copyOf(list);           // 从已有集合拷贝
ImmutableList.of("a", "b");           // 直接用值创建
ImmutableList.<String>builder().add("a").add("b").build();
```

#### Multiset（可重复、无序的「计数袋」）

既不像 `Set`（不能重复），也不强调 `List` 的顺序；适合**词频统计**。

```java
Multiset<Character> bag = HashMultiset.create();
for (char c : poem.toCharArray()) {
    bag.add(c);                 // 重复则计数 +1
}
bag.count('人');                // 出现次数
bag.elementSet();               // 不重复元素视图
bag.size();                     // 含重复的总个数
```

#### Sets / Lists 工具（高频）

```java
Set<Integer> a = Sets.newHashSet(1, 2, 3);
Set<Integer> b = Sets.newHashSet(3, 4);

Sets.union(a, b);          // 并集
Sets.intersection(a, b);   // 交集
Sets.difference(a, b);     // 差集（在 a 不在 b）

Lists.reverse(list);       // 反转
Lists.partition(list, 3);  // 按大小拆成多段
```

> 补：Guava 还有 `Files` / Source-Sink 简化 IO（创建后常自动关流）；布隆过滤器等进阶可后补。现代项目也常用 Hutool、Commons Lang，思路类似：少写样板代码。

### 4.1 练习 ★★★★

1. `Optional.of` 和 `ofNullable` 差别？  
2. 不可变集合相对可变 List 传进方法，解决什么风险？  
3. `Multiset` 适合什么场景？`count` 做什么？  
4. `Sets.union` / `intersection` / `difference` 各是什么？  
5. 写出：创建含 `"x","y"` 的不可变 List。  

**答案**

1. `of` 禁止 null（立刻失败）；`ofNullable` 允许 null（变成 empty）。  
2. 方法内改集合会波及调用方；不可变则改不了。  
3. 统计元素出现次数；返回某元素计数。  
4. 并集、交集、差集。  
5. `ImmutableList.of("x", "y");`

## 第5章 验证框架

### 5.1 Bean Validation 入门与常用约束 ★★★★★

**痛点 → 解法**

手写一长串 `if (x == null) throw …`：重复、散落各层、难维护。  
**Bean Validation**：把规则用注解写在 JavaBean 字段上，统一调用校验器；业务代码只关心「校验结果」。

| 对比 | 分层手写校验 | Bean 校验 |
|------|--------------|-----------|
| 规则放哪 | 每层各写一遍 | 域模型上注解 |
| 代码 | 又长又重复 | 声明式，一处定义多处复用 |

**规范与实现（记名字即可）**

- **JSR 303 / 349 / 380** → Bean Validation 1.0 / 1.1 / 2.0（规范）
- **Hibernate Validator**：最常用的**实现**（与 Hibernate ORM 无关，只是同团队）
- **Spring Validation**：在 HV 上再封装；Boot 里常见 `@Valid` / `@Validated`

#### 常用注解（三类）

**空值**

| 注解 | 含义 | 典型坑 |
|------|------|--------|
| `@NotNull` | 不能为 null | `""` 能过 |
| `@NotEmpty` | 非 null 且长度/size > 0 | `"  "`（仅空格）能过 |
| `@NotBlank` | 非 null 且**去空格后**仍有内容 | 字符串必填优先用它 |

案例：密码标 `@NotBlank`，用户输入三个空格 → 失败；若误用 `@NotEmpty` → 可能通过。

**范围**

- `@Size(min=6, max=20)`：字符串长度 / 集合元素个数  
- `@Min(18)` / `@Max(60)`：数值上下界  
- `@Past` / `@Future`：日期过去 / 将来  

**其它**

- `@Email`：邮箱基本格式（不是真发信校验）
- `@Pattern(regexp="…")`：正则  
- `@Length`：Hibernate 扩展；标准侧优先用 `@Size`

```java
public class UserInfo {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank
    @Size(min = 6, max = 20, message = "密码长度 6～20")
    private String password;

    @Email
    private String email;

    @Min(18) @Max(60)
    private Integer age;

    @Size(min = 1, message = "至少一名好友")
    private List<UserInfo> friends;
}
```

#### 怎么跑起来（手动）

```java
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();

Set<ConstraintViolation<UserInfo>> set = validator.validate(userInfo);
if (!set.isEmpty()) {
    // 把 message 拼起来抛业务异常 / 返回前端
}
```

逻辑：注解 = 声明规则；`validate` = 执行；返回集合非空 = 有字段未通过。

> 补：分组（登录/注册不同规则）、方法入参/返回值校验、自定义注解（如 `@Phone`）下一小点；Boot 里多用 `@Valid` 挂在 Controller 参数上，底层仍是这套。

### 5.1 练习 ★★★★★

1. Bean Validation 相对手写 if 的核心收益？  
2. `@NotNull` / `@NotEmpty` / `@NotBlank` 对字符串差别？  
3. 密码要 6～20 位用哪个注解？  
4. Hibernate Validator 和 Hibernate ORM 是什么关系？  
5. `validator.validate(bean)` 返回空集合表示什么？  

**答案**

1. 规则声明在模型上，少重复、易复用。  
2. 不能 null；非空长度（空格可通过）；去空格后仍非空。  
3. `@Size(min=6, max=20)`（常配合 `@NotBlank`）。  
4. 无关；只是同团队，一个做校验实现，一个做 ORM。  
5. 全部约束通过。

### 5.2 级联 · 分组 · 自定义约束 ★★★★

上一节是「字段上挂内置注解 + validate」。本节三块进阶：嵌套对象也要验、同一字段不同场景不同规则、内置不够时自己写注解。

#### 级联校验 `@Valid`

只给集合标 `@Size(min=1)`：只保证「有元素」，**不管元素内部字段**。  
要连好友对象里的 `userId`、`userName` 一起验 → 在集合/嵌套字段上再加 `@Valid`：

```java
@Size(min = 1)
@Valid                          // 级联：进去验每个 friend
private List<UserInfo> friends;
```

现象：外层 `userId` 已赋值仍报「用户ID不能为空」→ 失败的是**集合里那个空壳 friend**。

#### 分组校验

同一字段：登录必填 `userId`，注册时还没有 ID → 用 **groups**。

```java
public interface LoginGroup {}
public interface RegisterGroup {}

@NotNull(groups = LoginGroup.class)
private Long userId;

@NotBlank(groups = RegisterGroup.class)
@Email(groups = RegisterGroup.class)
private String email;

// 只验登录相关
validator.validate(user, LoginGroup.class);
// 只验注册相关
validator.validate(user, RegisterGroup.class);
```

未写 `groups` 的注解属于 **Default** 组。

**组顺序 `@GroupSequence`**：希望先跑简单登录规则，通过后再跑耗时注册规则（前一组失败则不继续）：

```java
@GroupSequence({LoginGroup.class, RegisterGroup.class})
public interface OrderedChecks {}

validator.validate(user, OrderedChecks.class);
```

也可 `validate(user, LoginGroup.class, RegisterGroup.class)`，但**不保证组间先后**，按属性排布跑。

#### 自定义约束（四步）

内置没有「手机号」→ 自己做。完整流程：

1. **定义注解**（`@Constraint(validatedBy = …)` 绑验证器）  
2. **实现 `ConstraintValidator`**（写 `isValid`）  
3. **字段上使用**  
4. **调用 `validate`**

```java
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    String message() default "手机号格式错误";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return true;   // 空留给 @NotBlank；避免 NPE
        return value.matches("158\d+"); // 课例简化：158 开头
    }
}

// 使用
@Phone
private String phone;
```

逻辑：注解声明「要什么」；Validator 实现「怎么判」；空值策略与 `@NotNull`/`@NotBlank` 搭配，别在自定义里 silently 当通过又当失败搞混。

> 补：方法入参/返回值校验（`validateParameters` 等）写法繁琐，生产里多靠 Spring AOP / `@Validated` 统一切；思想仍是「声明约束 + 执行器」。

### 5.2 练习 ★★★★

1. `@Size`  alone 和再加 `@Valid` 对集合差别？  
2. 登录要 `userId`、注册不要，怎么做？  
3. `@GroupSequence` 解决什么问题？  
4. 自定义约束最少哪四步？  
5. 自定义 `isValid` 里 value 为 null 时常见怎么处理？  

**答案**

1. 只验个数 vs 再验每个元素内部字段。  
2. `groups = LoginGroup.class`，调用时传入该组。  
3. 多组按顺序验，前组失败可短路。  
4. 定义注解 → 写 Validator → 字段使用 → validate。  
5. 返回 true 交给 `@NotNull`/`@NotBlank`，或按业务明确失败；课例用空串避免 NPE。

## 第6章 线程池

### 6.1 核心参数 · 处理流程 · 拒绝策略 ★★★★★

**是什么 / 为什么**

事先准备一批线程放进「池」：用完不销毁、丢回池里复用。  
相对「每个请求 `new Thread`」：

1. **降耗** — 少创建/销毁线程  
2. **提速** — 任务来了往往立刻有线程跑  
3. **可管** — 线程数有上限，避免把机器打崩  

案例：Word 转 PDF 很慢，100 个请求各建一个线程 → 上百线程同时睡/转，机器扛不住；改线程池后任务排队、线程数可控。

#### 七个核心参数（`ThreadPoolExecutor`）

| 参数 | 作用 |
|------|------|
| `corePoolSize` | 核心线程数（常驻倾向） |
| `maximumPoolSize` | 线程数上限 |
| `keepAliveTime` + 单位 | 非核心空闲多久回收 |
| `workQueue` | 任务阻塞队列 |
| `threadFactory` | 怎么创建线程（可命名便于排查） |
| `handler` | 队列满且线程到顶时的**拒绝策略** |

#### 处理流程（必背顺序）

提交任务后：

1. **核心未满** → 新建线程执行  
2. **核心已满** → 任务进**队列**（此时一般不先扩到 max）  
3. **队列也满** 且未到 max → 再建线程执行  
4. **已到 max** → 走**拒绝策略**

易错点：核心=2、队列容量=10、max=5 时，队列里还有空位 → **继续入队，不会先扩到 5**；只有队列满了才扩线程。

#### 阻塞队列（选型记三类）

| 类型 | 特点 | 风险 |
|------|------|------|
| 有界（如 `ArrayBlockingQueue`） | 容量固定 | 满了触发扩线程/拒绝 |
| 无界（如无参 `LinkedBlockingQueue`） | 几乎一直能塞 | 任务堆积可把内存撑爆 |
| 同步移交 `SynchronousQueue` | 不存元素，手递手 | 常配合「可大量建线程」的池 |

#### 四种拒绝策略

队列满 **且** 线程数已到 max 时：

1. **AbortPolicy（默认）** — 抛 `RejectedExecutionException`  
2. **DiscardPolicy** — 默默丢掉新任务（调用方无感知）  
3. **DiscardOldestPolicy** — 丢掉队列里最旧的，再试新任务  
4. **CallerRunsPolicy** — **调用方线程**自己 `run`（会拖慢提交方，形成背压）

```java
new ThreadPoolExecutor(
    2, 5, 60, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(10),
    Executors.defaultThreadFactory(),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

> 补：`Executors.newFixed/Cached/Single` 下一小点；生产更推荐显式 `new ThreadPoolExecutor(...)`，避免无界队列隐患。

### 6.1 练习 ★★★★★

1. 线程池相对疯狂 `new Thread` 的三点好处？  
2. 提交任务时，核心满了下一步先干什么？  
3. 队列还有空位时，会不会立刻扩到 max？  
4. 默认拒绝策略是什么行为？  
5. `CallerRunsPolicy` 谁来跑任务？  

**答案**

1. 降耗、提速、可管理。  
2. 放入阻塞队列。  
3. 不会；队列满了才扩。  
4. Abort：抛拒绝异常。  
5. 提交任务的那个调用线程。

### 6.2 常用池 · 提交任务 · 生命周期 ★★★★

#### `Executors` 三种快捷池（会用，更要懂坑）

底层都是 `ThreadPoolExecutor`，只是参数组合不同：

| 方法 | 核心/最大 | 队列 | 含义 | 主要风险 |
|------|-----------|------|------|----------|
| `newCachedThreadPool` | 0 / 无限 | `SynchronousQueue` | 来任务就尽量新建线程，空闲可回收 | 任务暴增 → **线程爆炸** |
| `newFixedThreadPool(n)` | n / n | **无界** `LinkedBlockingQueue` | 固定 n 个线程 | 任务堆积 → **队列撑爆内存** |
| `newSingleThreadExecutor` | 1 / 1 | 无界队列 | 单线程顺序执行 | 同 Fixed：队列无界 |

案例：高峰期狂提交 → Cached 开几千线程把 CPU 打满；Fixed 线程虽稳，百万任务堆在无界队列里 OOM。

> 补：阿里巴巴规约等建议**禁止**直接用这三种快捷方法；生产显式 `new ThreadPoolExecutor(有界队列 + 拒绝策略)`。

#### 提交任务：`execute` vs `submit`

```java
ExecutorService pool = Executors.newFixedThreadPool(2);

// 1) 无返回值
pool.execute(() -> System.out.println(2 * 5));

// 2) 有返回值 → Future
Future<Integer> f = pool.submit(() -> {
    Thread.sleep(1000);
    return 2 * 5;
});
Integer result = f.get();   // 阻塞直到算完
```

| | `execute` | `submit` |
|--|-----------|----------|
| 入参 | `Runnable` | `Runnable` 或 `Callable` |
| 返回 | void | `Future`（可 `get` 结果） |
| 异常 | `Runnable` 不能抛受检异常，常需自己 try | `Callable.call` 可抛，异常进 Future |

`get()` 会**阻塞**调用线程，直到任务结束（或超时重载）。

#### 五种状态与关闭

| 状态 | 含义 |
|------|------|
| RUNNING | 接新任务，也处理队列 |
| SHUTDOWN | `shutdown()` 后：不接新任务，**队列里继续跑完** |
| STOP | `shutdownNow()` 后：不接新、**尽量中断在跑的、丢弃队列任务** |
| TIDYING | 任务都停了，整理中 |
| TERMINATED | 彻底终止 |

```java
pool.shutdown();      // 温和：做完再停
pool.shutdownNow();   // 强硬：可能丢已提交未执行的任务
```

面试常问：`shutdown` 与 `shutdownNow` 差在**是否丢弃队列中未执行任务**（以及是否尝试中断正在执行的）。

### 6.2 练习 ★★★★

1. Fixed 线程数固定，最大隐患是什么？  
2. Cached 最大隐患是什么？  
3. 要拿异步计算结果用哪个提交方法？  
4. `Future.get()` 什么行为？  
5. `shutdown` 和 `shutdownNow` 核心差别？  

**答案**

1. 无界队列堆积导致 OOM。  
2. 线程数可无限增长拖垮系统。  
3. `submit`（拿 `Future`）。  
4. 阻塞直到任务完成（或超时）。  
5. 前者等队列跑完；后者尝试立刻停并可能丢队列任务。

## 第7章 Lombok

### 7.1 原理与常用注解 ★★★★

**是什么**

用注解在**编译期**自动生成样板代码：getter/setter、`toString`、`equals`/`hashCode`、构造器、日志字段等。  
写源码时只见注解；看 `target` 里反编译的 `.class` 才有生成方法。

**怎么做的（原理一句话）**

注解分两种解析：**运行时**（反射，如部分 Spring）与**编译时**。  
Lombok 走编译时：**插入式注解处理器（JSR 269）** 改 AST → 再编译成字节码。  
因此：IDE 必须装 Lombok 插件，否则编辑器以为「没有 getXxx」。

#### 常见方法类（最高频）

| 注解 | 作用 |
|------|------|
| `@Getter` / `@Setter` | 生成 get/set（可标类或字段） |
| `@ToString` | 生成 `toString`（可 `exclude` / `of`） |
| `@EqualsAndHashCode` | 生成 equals + hashCode |
| `@Data` | 上面四件套合集（再加所需参数构造等，见文档） |

```java
@Data
public class User {
    private Long id;
    private String name;
}
```

案例：手写 get/set/`toString` 几十行 → 一个 `@Data`；新增字段后这些方法自动跟上，少漏改。

#### 构造器

| 注解 | 生成 |
|------|------|
| `@NoArgsConstructor` | 无参构造 |
| `@AllArgsConstructor` | 全参构造 |
| `@RequiredArgsConstructor` | **必填**参数：`final` 字段 + 标了 `@NonNull` 的字段 |

#### 其它实用

| 注解 | 作用 |
|------|------|
| `@NonNull` | 入参/字段生成空检查，null 则立刻 NPE（快速失败） |
| `@Cleanup` | 编译成 try-finally 关流（思路同 try-with-resources） |
| `@Slf4j` | 生成 `private static final Logger log = …` |
| `@Builder` | 建造者模式，链式设属性 |

> 补：`@val` 是 Lombok 本地类型推断；Java 10+ 直接用 `var` 即可。`@Cleanup` 能用 TWR 时优先标准写法。

#### 优缺点

**利：** 少样板、属性变更少漏改、类更短。  
**弊：** 源码「不完整」、排错可能指到「不存在的行」、强依赖 IDE 插件；`@Data` 对实体生成 equals/hashCode 在 JPA 等场景要谨慎。

### 7.1 练习 ★★★★

1. Lombok 在编译期还是运行期生成代码？  
2. `@Data` 大致包含哪些能力？  
3. `@RequiredArgsConstructor` 会为哪些字段生成构造参数？  
4. 为什么 IDEA 不装插件会报红？  
5. `@Cleanup` 和 try-with-resources 关系？  

**答案**

1. 编译期（注解处理器改 AST）。  
2. Getter/Setter、ToString、EqualsAndHashCode 等合集。  
3. `final` 与 `@NonNull` 字段。  
4. 编辑器看不到尚未生成的方法。  
5. 都是自动关资源；现代代码优先 TWR。  
