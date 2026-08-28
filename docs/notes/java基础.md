# Java 基础

## 第1章 课程引言

### 1.1 发展历程 ★


| 年    | 事             |
| ---- | ------------- |
| 1990 | Sun「绿计划」      |
| 1992 | Oak           |
| 1995 | 正式发布 Java 1.0 |
| 2009 | Oracle 收购 Sun |
| 2011 | Java 7        |


### 1.2 技术体系平台 ★


| 平台      | 用途                              |
| ------- | ------------------------------- |
| Java SE | 标准版，核心 API；日常开发底座               |
| Java EE | 企业 Web（Servlet 等）；现多被 Boot 替代说法 |
| Java ME | 老式移动端；现基本不碰                     |


### 1.3 JVM ★★★

- 虚拟机：跑字节码、管内存
- 每 OS 一套 JVM，屏蔽硬件 / 系统差异
- 支撑「一次编译，到处运行」

### 1.4 JDK 与 JRE ★★★

```text
JDK ⊃ JRE ⊃ JVM

JDK = JRE + 开发工具（javac、javadoc…）  → 开发装这个
JRE = JVM + 核心类库                   → 只跑程序
```

装了 JDK 即含 JRE，一般不必再单独装 JRE。

### 1.5 练习 ★★

#### 口述题

1. JDK、JRE、JVM 谁包含谁？
2. 为什么「一次编译，到处运行」？

#### 答案

1. JDK ⊃ JRE ⊃ JVM（JDK 含 JRE，JRE 含 JVM）。
2. 源码先编成字节码（与平台无关），各 OS 用各自的 JVM 执行，所以一次编译、到处运行。

## 第2章 环境搭建

### 2.1 安装 JDK ★★★

1. 下载 JDK（按本机 OS 选安装包）。
2. 安装后配置环境变量（Windows）：
  - 新建系统变量 `JAVA_HOME` = JDK 安装目录
  - `Path` 增加 `%JAVA_HOME%\bin`
3. 验证：

```bash
java -version
javac -version
```

能打出版本号即成功。本机已验证：**JDK 21.0.8**（`java` / `javac` 均可用）。

### 2.2 安装 IntelliJ IDEA ★★★

1. 下载 IDEA（Community 免费版即可）。
2. 安装时可选：桌面快捷方式、关联 `.java`。
3. 打开 IDEA → 确认 Project SDK 指向本机 JDK。

### 2.3 练习 ★★

#### 口述题

1. 装完 JDK 后，为什么还要配 `JAVA_HOME` 和 `Path`？
2. `java` 和 `javac` 分别干什么？

#### 答案

1. 让系统在任意目录都能找到 JDK；`JAVA_HOME` 指安装根目录，`Path` 里加 `bin` 才能直接敲命令。
2. `javac` 编译 `.java` → `.class`；`java` 启动 JVM 运行 `.class` / 程序。

## 第3章 程序入门

### 3.1 程序结构与 main ★★★★

- 入口方法签名固定：

```java
public static void main(String[] args) { }
```

- 流程：写 `.java` → `javac` 出 `.class` → `java` 运行（从 `main` 开始）。

### 3.2 变量 ★★★★

```java
数据类型 变量名 = 初始值;
```

- 先声明（并赋值）再使用；同作用域内变量名不重复。
- `+`：两边都是数字 → 加法；有一边是字符串 → **拼接**。

### 3.3 基本类型与 null ★★★★


| 类型 | 关键字 | 备注 |
|------|--------|------|
| 整数 | `int` | 最常用 |
| 长整数 | `long` | 后缀 `L`；`byte`/`short` 少用 |
| 小数 | `double` | 常用；`float` 后缀 `F` |
| 布尔 | `boolean` | `true` / `false` |
| 字符 | `char` | 单引号 `'A'` |


引用类型（类、数组、`String`）变量未指向对象时可以是 `**null**`；对 `null` 调方法 → **NPE**。

### 3.4 标识符与命名 ★★★

- 标识符：字母 / 数字 / `_` / `$`；不能数字开头；区分大小写；不能用关键字。
- 约定：类名 `PascalCase`；方法/变量 `camelCase`；常量 `UPPER_SNAKE`。

### 3.5 注释 ★★


| 写法         | 用途   |
| ---------- | ---- |
| `//`       | 单行   |
| `/* … */`  | 多行   |
| `/** … */` | 文档注释 |


注释不参与编译运行。

### 3.6 常量 ★★★

```java
final 类型 常量名 = 值;   // 如 final double PI = 3.14;
```

运行中不可改；名字通常全大写。

### 3.7 练习 ★★★★

#### 口述题

1. `main` 方法完整写法是什么？起什么作用？
2. 变量和常量有什么区别？
3. `null` 用在什么上？对 `null` 调用方法会怎样？

#### 答案

1. `public static void main(String[] args)`；JVM 从此入口启动程序。
2. 变量运行中可改；`final` 常量赋值后不可改。
3. 引用类型可以是 `null`（未指向对象）；对其调用方法会 **NullPointerException**。

## 第4章 运算符与类型转换

### 4.1 算术运算 ★★★


| 运算符             | 含义   |
| --------------- | ---- |
| `+` `-` `*` `/` | 加减乘除 |
| `%`             | 求余   |


整数相除结果仍是整数（截断小数）。`/` 两边有一边是小数时按浮点除。

### 4.2 关系运算 ★★★★

结果是 `boolean`。注意：`=` 赋值，`==` 比较是否相等。


| 运算符               | 含义       |
| ----------------- | -------- |
| `==` `!=`         | 等于 / 不等于 |
| `>` `<` `>=` `<=` | 大小比较     |


### 4.3 自增自减 ★★★


| 写法            | 含义       |
| ------------- | -------- |
| `++a` / `--a` | 先变再参与表达式 |
| `a++` / `a--` | 先参与表达式再变 |


只能用于变量，不能用于常量。

### 4.4 赋值运算 ★★★

`=` 以及 `+=` `-=` `*=` `/=` `%=`（复合赋值隐含类型转换）。

### 4.5 自动类型转换 ★★★★

小范围 → 大范围可自动提升，例如 `int` → `long` → `float` → `double`。

运算时：参与方会先提升到较大类型再算。`boolean` 不能与数值互相转换。

### 4.6 强制类型转换 ★★★★

大范围 → 小范围必须强转，可能丢精度 / 溢出：

```java
int n = (int) 3.9;   // 结果 3
```

### 4.7 优先级 ★★

常见顺序（高→低）：`()` → `++/--` → `* / %` → `+ -` → 关系 → `== !=` → 赋值。拿不准就加括号。

### 4.8 练习 ★★★

#### 口述题

1. `=` 和 `==` 有什么区别？
2. `int a = 5 / 2;` 结果是多少？为什么？
3. 什么时候必须强制类型转换？

#### 答案

1. `=` 赋值；`==` 判断是否相等（结果 `boolean`）。
2. `2`；两个 `int` 相除按整数除，小数部分丢弃。
3. 把大范围类型赋给小范围类型时，如 `double` → `int`，需 `(目标类型)` 强转。

## 第5章 条件判断与方法

### 5.1 逻辑运算 ★★★★


| 运算符  | 含义                    |
| ---- | --------------------- |
| `&&` | 与：两边都 true 才 true（短路） |
| `||` | 或：一边 true 即 true（短路）  |
| `!`  | 非：取反                  |


### 5.2 if / else ★★★★

```java
if (条件) {
    // true
} else if (其他条件) {
    // …
} else {
    // 都不满足
}
```

可嵌套；复杂逻辑先画分支再写代码。

### 5.3 三元运算符 ★★★

```java
结果 = 条件 ? 值1 : 值2;
```

等价于简单的 if-else 赋值；多语句仍用 if。

### 5.4 switch ★★★★

```java
switch (表达式) {
    case 值1:
        // …
        break;
    case 值2:
        // …
        break;
    default:
        // …
}
```

- 表达式常见：`int`、`String`、枚举等（不是 boolean）。
- `case` 值须是常量；忘记 `break` 会**穿透**到下一 case。

### 5.5 方法 ★★★★

```java
返回值类型 方法名(参数列表) {
    // …
    return 结果;   // void 可省略 return 值
}
```

调用：`方法名(实参);`  
作用：把一段功能抽出去，复用、好读。

### 5.6 练习 ★★★★

#### 口述题

1. `&&` 和 `||` 有什么区别？什么叫短路？
2. `switch` 里为什么常写 `break`？
3. 方法的「返回值类型」是什么意思？`void` 表示什么？

#### 答案

1. `&&` 要两边都真；`||` 一边真即可。短路：左边已能定结果时不再算右边。
2. 防止 case 穿透，执行完当前分支后跳出 `switch`。
3. 方法算完交给调用方的数据类型；`void` 表示无返回值。

## 第6章 循环

### 6.1 while ★★★

```java
while (条件) {
    // 循环体；体内要有能让条件变 false 的语句，否则死循环
}
```

先判断，再执行；次数未知时常用。

### 6.2 do-while ★★

```java
do {
    // 至少执行一次
} while (条件);
```

### 6.3 for ★★★★

```java
for (初始化; 条件; 迭代) {
    // 循环体
}
```

次数明确时首选（如遍历 0～n）。

### 6.4 break 与 continue ★★★★


| 语句         | 作用               |
| ---------- | ---------------- |
| `break`    | 立刻结束**整个**循环     |
| `continue` | 结束**本轮**，进入下一轮判断 |


### 6.5 循环嵌套 ★★★

外层跑 1 次，内层完整跑一圈。注意复杂度，别无脑三层嵌套。

### 6.6 Scanner ★

键盘输入：`new Scanner(System.in)`，如 `nextInt()` / `next()`。练手可认；业务代码里更常见接口入参，不必深挖。

### 6.7 练习 ★★★

#### 口述题

1. `for` 和 `while` 怎么选？
2. `break` 和 `continue` 有什么区别？
3. `do-while` 和 `while` 最大不同是什么？

#### 答案

1. 次数明确用 `for`；条件驱动、次数未知用 `while`。
2. `break` 跳出整个循环；`continue` 只跳过本轮剩余语句。
3. `do-while` 先执行再判断，至少执行一次；`while` 可能一次都不执行。

## 第7章 数组

### 7.1 数组概念 ★★

- 同类型元素的有序集合；下标从 **0** 开始。
- 长度创建后**不可变**；越界 → `ArrayIndexOutOfBoundsException`。

### 7.2 声明与创建 ★★

```java
int[] arr = new int[5];           // 长度 5，元素默认 0
int[] arr2 = {96, 25, 85, 66};  // 静态初始化
```

访问 / 修改：`arr[i]`；长度：`arr.length`（合法下标 `0 ~ length-1`）。

数组变量存的是**引用**；`a = b` 时两个名字可指向同一块内存。

### 7.3 遍历 ★★

```java
for (int i = 0; i < arr.length; i++) { … }   // 可按下标改元素
for (int x : arr) { … }                      // foreach，不改原数组元素
```

### 7.4 二维数组 ★★

```java
int[][] grid = new int[3][4];
int[][] scores = { {100, 99}, {80, 95} };
```

「数组的数组」；外层长度 `grid.length`，内层 `grid[i].length`。

### 7.5 冒泡排序 ★

相邻比较交换；了解思想即可，业务里用 `Arrays.sort` 或集合排序。

### 7.6 练习 ★★

#### 口述题

1. 数组下标从几开始？长度能不能改？
2. `arr.length` 和合法下标范围是什么关系？
3. `for` 下标遍历和 foreach 有什么使用差别？

#### 答案

1. 从 0 开始；创建后长度固定，不能改。
2. 长度为 n 时，合法下标是 `0`～`n-1`。
3. 下标 for 能改元素；foreach 适合只读遍历，不靠下标。

## 第8章 类与对象

### 8.1 类与对象 ★★★★★


| 概念  | 含义                        |
| --- | ------------------------- |
| 类   | 模板：有哪些属性（成员变量）、能做什么（成员方法） |
| 对象  | 按模板用 `new` 造出来的实例         |


```java
类名 对象名 = new 类名();
对象名.成员变量;
对象名.成员方法();
```

对象在**堆**；变量里存的是引用（地址）。

### 8.2 构造方法 ★★★★★

- 名字与类名相同；**无返回值类型**（连 `void` 也不写）。
- `new` 时自动调用，做初始化。
- 不写构造 → 编译器给无参构造；**一旦自己写了构造，默认无参就不再自动给**（需要无参就自己再写一个）。
- 多个构造靠**重载**；`this(…)` 调本类其他构造，且必须在第一行。

### 8.3 this ★★★★★

`this` = 当前对象。成员与局部同名时：`this.成员` 区分开。

### 8.4 成员变量与局部变量 ★★★★


|     | 成员变量                   | 局部变量      |
| --- | ---------------------- | --------- |
| 位置  | 类里、方法外                 | 方法内 / 参数  |
| 默认值 | 有（如 int→0，引用→null）     | 无，使用前必须赋值 |
| 同名  | 方法内优先局部；用 `this.` 访问成员 |           |


### 8.5 方法重载 ★★★★

同名方法，**参数列表不同**（个数或类型）；与返回值无关。

### 8.6 匿名对象 ★★

`new Car().run();` 一次性用，无变量引用；少用，知道即可。

### 8.7 练习 ★★★★★

#### 口述题

1. 类和对象分别是什么？怎么创建对象？
2. 构造方法有哪些规则？
3. `this` 是什么？同名时怎么写？
4. 成员变量和局部变量有什么不同？
5. 什么叫方法重载？

#### 答案

1. 类是模板，对象是实例；`类名 名 = new 类名();`。
2. 与类同名、无返回类型；`new` 时调用；自定义构造后默认无参不再自动提供。
3. `this` 指当前对象；同名时写 `this.成员名`。
4. 成员在类里、有默认值；局部在方法里、使用前必须赋值。
5. 方法名相同、参数列表不同（个数或类型不同）。

## 第9章 字符串

### 9.1 创建 String ★★★★

```java
String a = "Golf";                 // 字面量 → 常量池，可复用
String b = new String("Golf");     // new → 堆上新对象
```

字面量优先用；`new String(...)` 一般不必手写。

### 9.2 == 与 equals ★★★★★


| 写法                 | 比什么          |
| ------------------ | ------------ |
| `==`               | 引用是否同一对象（地址） |
| `equals`           | 内容是否相同       |
| `equalsIgnoreCase` | 忽略大小写比内容     |


```java
"hi".equals(s);    // 推荐：字面量在前，避免 s 为 null 时 NPE
```

比较字符串内容**不要用 `==`**（面试必考）。

### 9.3 不可变 ★★★★★

`String` 创建后内容不可改；所谓「修改」其实是生成**新对象**。

频繁拼接用 `StringBuilder`（可变、`append`），不要循环里狂用 `+`。

### 9.4 常用方法 ★★★★


| 方法                            | 作用                   |
| ----------------------------- | -------------------- |
| `length()`                    | 长度（空串是 0；`null` 不能调） |
| `trim()`                      | 去首尾空白                |
| `substring(begin, end)`       | 截取，end 不含            |
| `indexOf` / `lastIndexOf`     | 查找                   |
| `replace`                     | 替换                   |
| `toLowerCase` / `toUpperCase` | 大小写                  |
| `+` / `concat`                | 拼接                   |


### 9.5 StringBuilder ★★★★

```java
StringBuilder sb = new StringBuilder();
sb.append("a").append("b");
String s = sb.toString();
```

另有 `insert` / `delete` / `replace` / `reverse`。

### 9.6 练习 ★★★★★

#### 口述题

1. 比较两个字符串内容该用什么？为什么不用 `==`？
2. 为什么说 String 不可变？
3. 循环里大量拼接字符串该用什么？

#### 答案

1. 用 `equals` 比内容；`==` 比的是引用地址，常量池与 `new` 场景下容易误判。
2. 创建后字符序列不能改，任何修改都会产生新 String 对象。
3. 用 `StringBuilder`（或 `StringBuffer`），避免反复创建临时 String。

## 第10章 封装

### 10.1 封装 ★★★★

把属性藏起来，只通过约定方法访问：

1. 成员变量加 `private`
2. 提供 `getXxx` / `setXxx`（可在 setter 里校验）

原则：不该对外暴露的细节不暴露；一个类尽量只干一件事（单一职责）。

### 10.2 访问控制 ★★★★

权限从大到小：`public` > `protected` > 默认（包内） > `private`


| 修饰符         | 本类  | 同包  | 子类  | 其他  |
| ----------- | --- | --- | --- | --- |
| `private`   | ✓   |     |     |     |
| 默认          | ✓   | ✓   |     |     |
| `protected` | ✓   | ✓   | ✓   |     |
| `public`    | ✓   | ✓   | ✓   | ✓   |


字段常 `private`；对外 API 用 `public`。

### 10.3 package ★★★

- 包 = 命名空间（对应目录）；防类名冲突、分类管理。
- 习惯：公司域名倒写，全小写，如 `com.ting.user`。
- 文件首行：`package …;`；用别的包：`import …;`。

### 10.4 static ★★★


|     | 实例成员    | 静态成员        |
| --- | ------- | ----------- |
| 归属  | 每个对象一份  | 类一份，对象共享    |
| 访问  | `对象.成员` | `类名.成员`（推荐） |
| 加载  | 随对象     | 随类加载，早于对象   |


注意：静态方法里**不能**用 `this`，一般只能直接访问静态成员。

`main` 是 `public static void`：不依赖对象即可由 JVM 启动。

### 10.5 参数传递 ★★★★

Java 只有**值传递**：

- 基本类型：传的是值副本，方法内改不影响外面。
- 引用类型：传的是引用副本（地址拷贝）；通过副本改**对象内部**会影响外面；若形参指向新对象，不影响实参变量本身。

### 10.6 可变参数 ★★

```java
void sum(int... nums) {   // 内部当 int[] 用
    for (int n : nums) { … }
}
sum(1, 2, 3);             // 个数可变
```

- 只能放在参数列表**最后**；一个方法最多一个。
- `main` 的 `String[] args` 是数组，不是可变参数写法；可变参数本质也是数组。

### 10.7 代码块 ★

局部 `{}`、构造代码块、`static {}`：认名字即可，少手写。

### 10.8 练习 ★★★★

#### 口述题

1. 封装通常怎么做？为什么字段要 `private`？
2. `public` / `private` 有什么区别？
3. `static` 成员和实例成员有什么不同？
4. Java 参数传递是「值传递」还是「引用传递」？
5. 可变参数写在参数列表的什么位置？

#### 答案

1. 字段 `private`，用 getter/setter 访问；防止外部乱改，可集中校验。
2. `public` 到处可访问；`private` 仅本类。
3. 静态属类、共享、用类名访问；实例属对象、各对象独立。
4. 只有值传递；引用类型传的是引用的副本。
5. 只能在最后；方法内当数组用。

## 第11章 继承与多态

### 11.1 继承 ★★★★

子类用 `extends` 继承父类，复用字段与方法；Java **单继承**（一个类只能直接继承一个父类）。

```java
class Car { void run() { … } }
class EvCar extends Car { void charge() { … } }
```

所有类最终继承 `Object`（如 `toString` / `equals`）。

### 11.2 super ★★★★

`super` = 父类。两种用法：

| 写法 | 含义 | 要点 |
|------|------|------|
| `super(...)` | 调父类构造 | 必须在子类构造**第一行** |
| `super.成员` | 访问父类字段 / 方法 | 重写后仍要父类逻辑时用 |

```java
class Student extends Person {
    Student(String name, int grade) {
        super(name);           // 先父构造，再子构造
        this.grade = grade;
    }
    @Override
    void speak() {
        super.speak();         // 先父类方法，再扩展
        System.out.println("…");
    }
}
```

- `new 子类()`：**先父构造 → 再子构造**（不写则自动 `super()`）
- 父类只有有参构造时，子类必须显式 `super(实参)`
- 与 `this(...)` 都只能在第一行，同一构造里二选一
- 父类 `private` 成员访问不到；静态方法里无 `super`

### 11.3 方法重写 ★★★★★

子类定义与父类**同签名**的实例方法，覆盖父类实现。常用 `@Override` 标注防写错。

- 重写 ≠ 重载：重写改实现；重载换参数列表。
- 访问权限不能更严；返回类型兼容。

### 11.4 final ★★★

| 修饰 | 效果 |
|------|------|
| `final` 类 | 不能被继承 |
| `final` 方法 | 不能被重写 |
| `final` 变量 | 只能赋一次 |

### 11.5 多态 ★★★★★

**一句话**：父类引用指向子类对象；调父类里有的方法时，优先走**子类重写**的实现。

```java
class Car {
    void run() { System.out.println("车跑"); }
}
class EvCar extends Car {
    @Override
    void run() { System.out.println("电车跑"); }  // 重写
    void charge() { System.out.println("充电"); } // 子类独有
}

Car c = new EvCar();  // 父类引用 → 子类对象
c.run();              // 打印「电车跑」（看真实对象，不是看左边类型）
// c.charge();        // 编译失败：左边是 Car，看不到 charge
```

#### 向上转型（自动、安全）

子类对象赋给父类变量，Java 自动完成；子类一定是父类类型，不会转错。

```java
EvCar ev = new EvCar();
Car c = ev;           // 向上：EvCar → Car，自动
// 等价：Car c = new EvCar();
```

#### 向下转型（要检查）

只有父类引用时，想调**子类独有**方法，必须再变回子类类型：

```java
Car c = new EvCar();
if (c instanceof EvCar) {     // 先确认真实是不是 EvCar
    EvCar ev = (EvCar) c;     // 向下：Car → EvCar，手动强转
    ev.charge();              // 可以调独有方法
}
// 现代写法：if (c instanceof EvCar ev) { ev.charge(); }
```

不检查就强转，类型不对会炸：

```java
Car c = new Car();    // 真实就是 Car，不是 EvCar
EvCar ev = (EvCar) c; // ClassCastException
```

| | 方向 | 典型写法 | 安全吗 |
|--|------|----------|--------|
| 向上 | 子 → 父 | `Car c = new EvCar();` | 自动、安全 |
| 向下 | 父 → 子 | `(EvCar) c` | 先 `instanceof`，否则可能异常 |

### 11.6 练习 ★★★★

#### 口述题

1. 继承用什么关键字？Java 能多继承类吗？
2. 重写和重载有什么区别？
3. 什么是多态？父类引用指向子类对象时调方法走谁的实现？
4. 向上转型和向下转型有什么区别？
5. 向下转型前为什么常用 `instanceof`？

#### 答案

1. `extends`；类只能单继承，不能同时 extends 多个类。
2. 重写：同签名覆盖父类方法；重载：同名不同参数。
3. 同一父类型引用、不同子类对象表现不同；优先走子类重写的方法。
4. 向上：子→父，自动安全；向下：父→子，需强转且先确认类型。
5. 确认对象真实类型，避免错误强转抛 `ClassCastException`。

## 第12章 抽象类与接口

### 12.1 抽象类 ★★★★

- 用 `abstract` 修饰类；**不能** `new` 抽象类。
- 可含普通方法 + **抽象方法**（只有签名、无方法体）。
- 子类必须实现所有抽象方法（除非子类也是 abstract）。

```java
abstract class Shape {
    abstract double area();
    void print() { System.out.println(area()); }
}
class Circle extends Shape {
    double r;
    @Override double area() { return Math.PI * r * r; }
}
```

### 12.2 接口 ★★★★★

**一句话**：接口规定「必须有哪些方法」；类用 `implements` 写出实现。不能 `new` 接口本身。

```java
interface Payable {
    void pay();   // 默认 public abstract，实现类必须写
}

class OrderService implements Payable {
    @Override
    public void pay() {
        System.out.println("订单支付");
    }
}

Payable p = new OrderService();  // 接口类型引用 → 实现类对象（也是多态）
p.pay();                         // 打印「订单支付」
// new Payable();                // 编译失败
```

一个类可实现**多个**接口（类只能单继承一个父类）：

```java
interface Payable { void pay(); }
interface Refundable { void refund(); }

class OrderService implements Payable, Refundable {
    @Override public void pay() { … }
    @Override public void refund() { … }
}
```

- 接口方法默认 `public abstract`（Java 8+ 还可有 `default` / `static`，先认即可）。
- 接口之间可以 `extends`（可多继承接口）。
- 工作中常写：`XxxService` 接口 + `XxxServiceImpl` 实现类。

### 12.3 抽象类 vs 接口 ★★★★★

| | 抽象类 | 接口 |
|--|--------|------|
| 实例化 | 不能直接 new | 不能直接 new |
| 继承/实现 | 单继承 `extends` | 可多实现 `implements` |
| 成员 | 可有普通字段、构造 | 字段多为常量；无构造 |
| 方法 | 可有抽象 + 普通方法 | 默认抽象；可有 `default` / `static` |
| 选用 | 有共用实现、强 is-a | 能力拼装、多类型共同行为 |

工作中：**优先面向接口**；抽象类用于抽公共模板。

### 12.4 内部类 ★

类里再定义类：成员内部类、静态内部类、匿名内部类。会认 `new 接口() { … }` 匿名实现即可，细节可后补。

### 12.5 练习 ★★★★

#### 口述题

1. 抽象类能不能直接 `new`？抽象方法有什么特点？
2. 一个类能实现几个接口？和继承类有什么不同？
3. 什么时候用接口，什么时候用抽象类？

#### 答案

1. 不能直接 `new`；抽象方法无方法体，由子类实现。
2. 可实现多个接口；类只能单继承一个父类。
3. 多能力组合、对外约定 → 接口；要复用部分实现、强继承关系 → 抽象类。

## 第13章 异常

### 13.1 异常体系 ★★★★★

```text
Throwable
├── Error          // 严重错误，一般不捕获（如 OOM）
└── Exception
    ├── 受检异常（编译期必须处理或声明）
    └── RuntimeException 及子类（运行时异常，可不强制声明）
```

业务里常见：`NullPointerException`、`IllegalArgumentException`、自定义业务异常。

### 13.2 try / catch / finally ★★★★★

```java
try {
    // 可能出错
} catch (具体异常 e) {
    // 处理；多 catch 时子类写前面
} finally {
    // 通常都会执行（关资源等）；避免在 finally 里 return
}
```

### 13.3 throw 与 throws ★★★★★

| 关键字 | 作用 | 位置 |
|--------|------|------|
| `throw` | **扔出**一个异常对象 | 方法**里面** |
| `throws` | **声明**本方法可能抛这些异常 | 方法**签名上** |

**throw**：主动制造异常（参数不合法就扔出去）

```java
void setAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("年龄不能为负");  // 扔出异常，后面不执行
    }
    this.age = age;
}
```

**throws**：方法可能抛受检异常时，在签名上告诉调用方「你要处理」

```java
void readFile() throws IOException {   // 声明：我可能抛 IOException
    // 读文件…（内部也可能 throw new IOException("…");）
}

void demo() {
    try {
        readFile();                    // 调用方必须 try-catch，或再 throws
    } catch (IOException e) {
        System.out.println("读失败: " + e.getMessage());
    }
}
```

一句话：`throw` = 扔；`throws` = 声明可能扔（多用于受检异常）。业务里更常 `throw new BizException(...)`（运行时异常，可不写 `throws`）。

```java
public class BizException extends RuntimeException {  // 业务常用运行时异常
    public BizException(String msg) { super(msg); }
}
```

- 受检：继承 `Exception`
- 非受检：继承 `RuntimeException`（Spring 项目更常见）

命名习惯：`XxxException`。

### 13.5 练习 ★★★★★

#### 口述题

1. `Error` 和 `Exception` 有什么区别？
2. `throw` 和 `throws` 分别干什么？
3. 受检异常和运行时异常有什么差别？
4. 为什么业务异常常继承 `RuntimeException`？
5. `finally` 里该不该写 `return`？

#### 答案

1. `Error` 是严重系统错误，通常不处理；`Exception` 可被捕获处理。
2. `throw` 抛出异常对象；`throws` 在方法签名上声明可能抛出的异常。
3. 受检：编译期必须处理或声明；运行时（`RuntimeException`）可不强制声明。
4. 不必层层强制声明，调用链更干净；由全局异常处理统一返回。
5. 不要；会掩盖 try 的返回值或异常。`finally` 多数情况会执行。

## 第14章 集合

### 14.1 集合是什么 ★★★★

装一组对象的容器；**长度可变**（数组长度固定）。

| | 数组 | 集合 |
|--|------|------|
| 长度 | 固定 | 可变 |
| 存什么 | 基本类型或引用 | 只能存引用（`int` 要先变成 `Integer`） |

三大类（先记这张表）：

| 类型 | 特点 | 默认用 |
|------|------|--------|
| `List` | 有序、可重复、有下标 | `ArrayList` |
| `Map` | key → value，key 唯一 | `HashMap` |
| `Set` | 不重复（次要） | `HashSet` |

### 14.2 List ★★★★★

**一句话**：一串元素，像可变长数组；有下标，可重复。

```java
List<String> list = new ArrayList<>();
list.add("张三");
list.add("李四");
list.add("张三");           // 允许重复
System.out.println(list.get(0));   // 张三（下标从 0）
System.out.println(list.size());   // 3

for (String name : list) {         // 最常用遍历
    System.out.println(name);
}
```

| 实现 | 怎么选 |
|------|--------|
| `ArrayList` | **默认用这个**（按下标查快） |
| `LinkedList` | 频繁在头/尾增删时再考虑 |

左边写接口、右边写实现：`List<String> list = new ArrayList<>();`（方便以后换实现）。

### 14.3 Map ★★★★★

**一句话**：用 key 找 value；key 不能重复，value 可以。

```java
Map<String, Integer> score = new HashMap<>();
score.put("语文", 90);
score.put("数学", 85);
score.put("语文", 95);              // 同一 key → 覆盖成 95

System.out.println(score.get("语文"));   // 95
System.out.println(score.get("英语"));   // null（没有这个 key）
System.out.println(score.containsKey("数学")); // true

for (Map.Entry<String, Integer> e : score.entrySet()) {
    System.out.println(e.getKey() + "=" + e.getValue());
}
```

工作默认用 `HashMap`；`TreeMap` 按 key 排序，认名字即可。

### 14.4 泛型 ★★★★

`<…>` 限定类型，编译期就能拦住放错：

```java
List<String> list = new ArrayList<>();
list.add("ok");
// list.add(1);   // 编译失败：只能放 String
```

不写泛型也能跑，但容易放错类型、取出还要强转——**集合一律写泛型**。

### 14.5 Set ★★

不重复、一般无下标；去重用。次于 List/Map。

```java
Set<String> set = new HashSet<>();
set.add("a");
set.add("a");   // 第二次无效，仍只有 1 个
```

### 14.6 怎么选 ★★★★★

| 需求 | 选 |
|------|----|
| 一串数据、可重复、要下标 | `List` → `ArrayList` |
| 按 key 查 value | `Map` → `HashMap` |
| 只要不重复 | `Set` → `HashSet` |

### 14.7 练习 ★★★★★

#### 口述题

1. `ArrayList` 和 `LinkedList` 怎么选？
2. `List` 和 `Map` 分别适合什么？
3. `Set` 适合什么场景？
4. `map.get(key)` 找不到时返回什么？
5. 为什么集合要用泛型？

#### 答案

1. 普通列表、按下标访问用 `ArrayList`；频繁头尾增删可考虑 `LinkedList`。
2. `List` 存一串元素；`Map` 存键值映射。
3. 去重、不关心下标时用 `Set`（常用 `HashSet`）。
4. 返回 `null`（注意与 value 本身为 null 区分）。
5. 限定类型，编译期检查，少强转、少类型错误。

## 第15章 常用类库

### 15.1 包装类 ★★★★

基本类型 ↔ 对象（才能进集合等）：


| 基本        | 包装          |
| --------- | ----------- |
| `int`     | `Integer`   |
| `long`    | `Long`      |
| `double`  | `Double`    |
| `boolean` | `Boolean`   |
| `char`    | `Character` |


```java
Integer a = 100;          // 自动装箱
int b = a;                // 自动拆箱
int n = Integer.parseInt("21");
```

注意：包装类可为 `null`，拆箱可能 NPE。`Integer` 对 **-128～127** 有缓存，`==` 比包装对象不可靠，比数值用 `equals` 或先拆箱。

### 15.2 BigDecimal ★★★★

金额等精确小数**不要用 `double` 硬算**，用 `BigDecimal`（字符串构造更稳）。

```java
new BigDecimal("0.1").add(new BigDecimal("0.2"));
```

### 15.3 日期时间 ★★★★

优先 **java.time**（JDK8+）：


| 类                   | 用途       |
| ------------------- | -------- |
| `LocalDate`         | 日期       |
| `LocalDateTime`     | 日期+时间    |
| `DateTimeFormatter` | 格式化 / 解析 |


旧的 `Date` / `Calendar` 会认即可，新代码少用。

### 15.4 枚举 ★★★

一组具名常量：`enum Status { DRAFT, OK }`，比魔法数字清晰。

### 15.5 其他 ★★

- `Math`：`max` / `min` / 取整等  
- `Arrays`：`sort` / `toString`  
- `Random`：随机数

需要时查 API，不必背全。

### 15.6 练习 ★★

#### 口述题

1. 为什么集合里常用 `Integer` 而不是 `int`？
2. 金额计算为什么常用 `BigDecimal`？
3. 新项目处理日期优先用哪套 API？

#### 答案

1. 集合存的是对象；`int` 是基本类型，需包装成 `Integer`（可自动装箱）。
2. `double` 有精度误差；金额要精确用 `BigDecimal`。
3. `java.time`（如 `LocalDateTime`），少用旧 `Date`。

## 第16章 多线程

### 16.1 进程与线程 ★★★


|     | 进程                 | 线程          |
| --- | ------------------ | ----------- |
| 角色  | 资源分配最小单位（一个运行中的程序） | 执行最小单位，属于进程 |
| 关系  | 可含多线程              | 共享进程资源      |


- **并发**：同一 CPU 时间片轮流切换  
- **并行**：多核真正同时执行

### 16.2 创建线程 ★★★

**方式一：继承 `Thread`**

```java
class T extends Thread {
    public void run() { /* 任务 */ }
}
new T().start();   // 用 start，不要只调 run
```

**方式二：实现 `Runnable`（更常用）**

```java
Runnable task = () -> { /* 任务 */ };
new Thread(task).start();
```

`start()` 才会真正起线程；直接 `run()` 只是普通方法调用。

### 16.3 线程池概念 ★★★

工作中很少手写 `new Thread`，多用线程池复用线程、控并发：

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
pool.execute(task);
pool.shutdown();
```

认「提交任务给池」即可；JUC 深水区后置。

### 16.4 同步与死锁 ★★

多线程改同一数据可能出错 → 加锁（`synchronized` / `Lock`）。  
死锁：互相等对方的锁。知道有这回事；业务里优先用现成并发工具，少自己玩锁。

### 16.5 练习 ★★★

#### 口述题

1. 进程和线程有什么区别？
2. 并发和并行有什么不同？
3. 为什么要用 `start()` 而不是直接 `run()`？
4. `Thread` 和 `Runnable` 哪种更常用？为什么？
5. 工作中为什么常用线程池？

#### 答案

1. 进程是资源分配单位；线程是执行单位，从属于进程。
2. 并发：时间片轮流切换；并行：多核真正同时执行。
3. `start()` 才会创建新线程异步执行；`run()` 只在当前线程同步调用。
4. 实现 `Runnable`（或用线程池）更常用：不占继承名额，任务与线程解耦。
5. 复用线程、限制数量，避免频繁创建销毁线程、防止线程爆炸。

## 第17章 IO

### 17.1 什么是 IO ★★★

Input / Output：从外部读入、向外部写出（文件、网络等）。


| 类型  | 适合           |
| --- | ------------ |
| 字节流 | 任意二进制（图、音视频） |
| 字符流 | 文本（按字符）      |


### 17.2 File ★★★

描述路径上的文件或目录（还不等于已读写内容）：

```java
File f = new File("d:/data/a.txt");
f.exists();
f.isFile();
f.length();
f.getAbsolutePath();
```

`mkdir` / `mkdirs`、`createNewFile`、`delete` 会认即可。

### 17.3 读文本 ★★★★

```java
try (BufferedReader br = new BufferedReader(new FileReader("a.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        // 处理一行
    }
}  // 自动 close
```

- `FileReader`：字符输入  
- `BufferedReader`：缓冲 + `readLine()`

优先 **try-with-resources**，少手写 `finally close`。

### 17.4 写文本 ★★★★

```java
try (BufferedWriter bw = new BufferedWriter(new FileWriter("a.txt", true))) {
    // true = 追加
    bw.write("hello");
    bw.newLine();
}
```

`flush` 刷新缓冲；`close` 会顺带 flush。

### 17.5 练习 ★★★

#### 口述题

1. 字节流和字符流大概怎么选？
2. 读文本文件常用哪两个类？
3. 为什么推荐 try-with-resources？

#### 答案

1. 二进制用字节流；纯文本用字符流。
2. `FileReader` + `BufferedReader`（按行用 `readLine`）。
3. 离开 try 块自动关闭流，避免忘记 `close` 导致资源泄漏。

## 第18章 Maven

> 工具课：为 Spring Boot 铺路。本章只学 Maven；Boot 注解 / 启动类等归阶段 2（474）。

### 18.1 Maven 是什么 ★★★★

自动化构建 + **依赖管理**工具：

- 声明要用的库（依赖），自动下载到本地仓库  
- 统一编译、打包（`jar` / `war`）  
- 约定目录：`src/main/java`、`src/main/resources`、`src/test/java`

配置文件：根目录 `**pom.xml**`（Project Object Model）。

### 18.2 坐标 GAV ★★★★★

每个构件用三元组唯一标识：


| 字段           | 含义       | 本仓库例             |
| ------------ | -------- | ---------------- |
| `groupId`    | 组织 / 公司  | `com.ting`       |
| `artifactId` | 项目 / 模块名 | `ting-user`      |
| `version`    | 版本       | `1.0.0-SNAPSHOT` |


依赖也是靠 GAV 找包，例如 `spring-boot-starter-web`。

### 18.3 常用命令 ★★★★


| 命令 | 作用 |
|------|------|
| `mvn clean` | 清 `target` |
| `mvn compile` | 编译 |
| `mvn test` | 跑测试 |
| `mvn package` | 打包到 `target` |
| `mvn install` | 装到本地仓库 `~/.m2` |


IDEA 里点 Maven 面板等同于跑这些命令。

### 18.4 依赖与多模块 ★★★★

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

- **父 POM**（`packaging=pom`）：统一版本、`modules` 列表  
- **子模块**：`<parent>` 指向父工程，继承依赖管理

本仓库：`ting-full` 父工程 → `ting-common` / `ting-user` / `ting-biz` / `ting-gateway`。

`dependencyManagement`：只锁定版本，子模块声明依赖时可不写 version。

### 18.5 练习 ★★★★

#### 口述题

1. Maven 主要解决什么问题？
2. `groupId`、`artifactId`、`version` 分别是什么？
3. 本仓库为什么有一个父 `pom.xml` 和多个子模块？
4. `mvn package` 大概做什么？
5. `mvn install` 和 `package` 有什么不同？

#### 答案

1. 管理依赖与构建（编译打包），不必手工下 jar。
2. 组织名、模块名、版本号，合称坐标，用来定位构件。
3. 父工程统一版本与模块列表；子模块各管一块业务，依赖可复用（如 common）。
4. 编译并打出包（如 jar）到 `target` 目录。
5. `install` 还会装进本地仓库 `~/.m2`，供其他模块依赖；`package` 只打到本模块 `target`。

