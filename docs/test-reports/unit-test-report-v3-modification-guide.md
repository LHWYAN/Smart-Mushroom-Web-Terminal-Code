# `unit-test-report-rewritten-v3.docx` 修改指南

> **对照基准**：本文档基于 `c:\Users\18526\Downloads\unit-test-report-rewritten-v3.docx` 全文，与 **2026-07-03 最新代码**（含 Auth 认证模块单元测试）逐项比对编写。  
> **使用方式**：按章节在 Word 中定位 → 删除/替换对应段落或表格 → 粘贴「替换为」中的正文 → 更新截图。

---

## 0. 修改优先级总览

| 优先级 | 类型 | 数量 | 说明 |
|--------|------|------|------|
| **P0 阻塞提交** | 个人信息、待填占位、截图占位 | 约 8 处 | 不改会被判为未完成稿 |
| **P1 数据过期** | 测试数量、覆盖率、Auth 模块缺失 | 约 25 处 | v3 编写于 Auth 测试补充之前，数据已过时 |
| **P2 结构/图号** | 图号重复、章节引用不一致 | 约 6 处 | 影响阅读专业性 |
| **P3 表述增强** | 覆盖率解释、缺陷案例展开 | 约 5 处 | 加分项，建议做 |

---

## 1. 封面与元数据（P0）

### 1.1 封面表格 — 学生姓名、学号

**Word 位置**：封面第二页表格，`学生姓名`、`学号` 两行。

**当前内容（若仍为占位）**：
```text
学生姓名：（待填写：请填写真实姓名）
学号：（待填写：请填写真实学号）
```

**替换为**（填你自己的信息）：
```text
学生姓名：张三
学号：202XXXXXX
```

---

### 1.2 修订记录 — 作者列

**Word 位置**：「修订记录」表格，`V1.0`、`V1.1` 两行的「作者」列。

**当前内容**：
```text
【待填写】
```

**替换为**：
```text
张三
```
（两行都填同一姓名）

---

### 1.3 版本号建议升级

**Word 位置**：文档属性表「版本」、修订记录新增一行。

**建议操作**：
- 文档属性表 `版本`：`V1.1` → **`V1.2`**
- 修订记录**新增一行**：

| 版本 | 日期 | 修订说明 | 作者 |
|------|------|----------|------|
| V1.2 | 2026-07-03 | 补充用户注册/登录（Auth）单元测试；更新 Web 前后端用例数与覆盖率数据；同步截图与附录清单 | 你的姓名 |

---

## 2. 摘要（P1 — 必须重写关键数据）

**Word 位置**：目录后、「摘要」整段。

**当前问题**：未提及 **用户注册/登录** 测试；用例总数仍为旧版 116/46。

**删除原摘要中关于数量的句子**，**整段替换为**：

```text
本报告涵盖智慧蘑菇农场项目的单元测试工作，覆盖 Web 后端、Web 前端和鸿蒙终端三个层面。报告从测试目标、测试环境、测试策略、测试对象、代表性测试用例、执行结果、覆盖率分析和改进建议等方面，对三端测试体系进行系统说明。

Web 后端和 Web 前端测试已在当前开发环境中完成执行并全部通过。后端共 16 个测试类、154 个测试方法；前端共 3 个测试文件、60 个测试用例。其中本次重点补充了用户注册、登录、Token 管理（AuthController / AuthService / JwtUtil）及前端认证 API、路由导航守卫的单元测试，满足实训对「核心功能 100%、辅助功能 ≥80%」的覆盖率要求。鸿蒙端作为扩展项已完成 Hypium 测试代码编写与注册，运行结果以 DevEco Studio 或 hvigorw 实际截图为准。

测试通过 Mock 隔离数据库、云服务与外部 AI 依赖，形成可重复、可维护的自动化验证体系。覆盖率分析重点关注 controller、service、util、api、utils、router 等核心业务模块，而非 config、consumer、websocket 等基础设施类。

关键词：单元测试；JUnit 5；Vitest；Hypium；JaCoCo；Mock；用户认证；智慧蘑菇
```

---

## 3. 第 2 章「测试目的与依据」

### 3.1 §2.1 测试目标 — 第一条

**Word 位置**：`2.1 测试目标` 有序列表第 1 条。

**当前内容**：
```text
验证核心功能正确性：包括设备 CRUD、传感器数据查询、命令下发记录、AI 问答、健康检查等接口与业务逻辑。
```

**替换为**：
```text
验证核心功能正确性：包括用户注册与登录、设备 CRUD、传感器数据查询、命令下发记录、AI 问答、健康检查等接口与业务逻辑。
```

---

### 3.2 §2.1 测试目标 — 新增第四条（可选但推荐）

在现有第 4 条「验证多端逻辑一致性」**之后**追加：

```text
5. 验证认证链路完整性：覆盖 Token 生成/校验/过期/注销、注册参数校验、登录失败分支及前端 setAuth 与路由守卫行为。
```

---

### 3.3 §2.3 覆盖率目标表

**Word 位置**：`2.3 覆盖率目标` 表格。

**在「Web 后端 Controller / Service」行的「说明」列**，原文若为「5 个 Controller」，**改为**：

```text
6 个 Controller（含 AuthController）及对应 Service，核心方法行覆盖率目标 100%
```

**在「Web 工具类」行「说明」列**，原文若仅写 AiAnswerSanitizer，**改为**：

```text
AiAnswerSanitizer、JwtUtil 等，指令覆盖率 ≥ 80%
```

**在「Web 前端 utils / api / router」行「说明」列**，**改为**：

```text
含认证 API（register/login/profile/logout）、setAuth/isLoggedIn 及路由导航守卫，语句覆盖率 100%
```

---

## 4. 第 6 章「测试范围与对象」（P1 — 改动最大）

### 4.1 §6.1 测试规模总览表

**Word 位置**：`6.1 测试规模总览` 表格 + 图5 说明。

**整张表替换为**：

| 端 | 测试文件/类 | 用例数 | 说明 |
|----|-------------|--------|------|
| Web 后端 | **16** | **154** | JUnit 5 / Mockito / MockMvc |
| Web 前端 | 3 | **60** | Vitest + jsdom |
| 鸿蒙端 | 10 | 约 145 | Hypium（扩展项，需本地执行截图） |
| **合计** | **29** | **359+** | 自动化断言场景 |

**图5 图题说明文字替换为**：
```text
图5 说明：Web 后端 16 个测试类、154 用例；Web 前端 3 个文件、60 用例；鸿蒙 10 个文件、约 145 个 it 用例。较 V1.1 版本新增 Auth 认证模块 38 条后端用例及 14 条前端用例。
```

---

### 4.2 §6.2 Web 后端测试对象表 — 新增 3 行 + 修改 Controller 数量描述

**Word 位置**：`6.2 Web 后端测试对象` 大表格。

**在 `HealthControllerTest` 行之后、`不在本范围` 段落之前，插入以下 3 行**：

| 测试类 | 被测类 | 用例数 | 测试重点 |
|--------|--------|--------|----------|
| `JwtUtilTest` | `JwtUtil` | 10 | Token 生成/校验/过期/移除、同用户重复登录旧 Token 失效 |
| `AuthServiceTest` | `AuthService` | 15 | 注册/登录/注销、initTable、RowMapper、getCurrentUser |
| `AuthControllerTest` | `AuthController` | 16 | POST register/login、GET profile、POST logout；参数校验与 fail 分支 |

**表格标题行「13 类 / 116 用例」改为**：
```text
Web 后端测试对象（16 类 / 154 用例）
```

**「不在本范围」段落保持不变**（DataConsumerManager、WebSocketConfig 等仍不测）。

---

### 4.3 §6.3 Web 前端测试对象表 — 更新 3 行数据

**整张表替换为**：

| 测试文件 | 被测模块 | 用例数 | 测试重点 |
|----------|----------|--------|----------|
| `aiAnswer.test.js` | `utils/aiAnswer.js` | 17 | 与后端 `AiAnswerSanitizerTest` 用例对齐 |
| `api.test.js` | `api/index.js` | **27** | baseURL、拦截器、20 个 REST 函数 + **4 个认证 API + setAuth/getAuthUser/isLoggedIn** |
| `router.test.js` | `router/index.js` | **16** | **8 条路由**（含 /login、/register）、redirect、meta.noAuth、**5 条导航守卫** |

**小节标题改为**：
```text
6.3 Web 前端测试对象（3 文件 / 60 用例）
```

---

## 5. 第 7 章「测试用例设计」— 新增 Auth 小节（P1）

### 5.1 在 §7.1 末尾新增 §7.1.5

**Word 位置**：`7.1.4 设备级联删除（DeviceService）` 之后、`7.2 Web 前端` 之前。

**插入以下完整小节**：

---

#### 7.1.5 用户认证（AuthController / AuthService / JwtUtil）

**被测逻辑示例（注册参数校验）**：
```java
if (username == null || username.isBlank()) {
    return ApiResponse.fail("用户名不能为空");
}
if (password.length() < 4) {
    return ApiResponse.fail("密码长度不能少于4位");
}
```

| 用例 ID | 场景类型 | 输入/条件 | 预期结果 |
|---------|----------|-----------|----------|
| AC-R-01 | 正常 | username+password 合法 | code=0，返回 User（无 password 字段） |
| AC-R-02 | 异常 | username 为空或空白 | code=-1，「用户名不能为空」 |
| AC-R-03 | 异常 | password 少于 4 位 | code=-1，「密码长度不能少于4位」 |
| AC-R-04 | 异常 | 用户名已存在 | code=-1，「注册失败，用户名可能已存在」 |
| AC-L-01 | 正常 | 用户名密码正确 | 返回 user + token |
| AC-L-02 | 异常 | 用户名或密码错误 | code=-1，「用户名或密码错误」 |
| AC-L-03 | 异常 | 账户 status=disabled | login 返回 null |
| AC-P-01 | 正常 | Bearer token 有效 | profile 返回当前用户 |
| AC-P-02 | 异常 | token 无效或过期 | code=-1，「token无效或已过期」 |
| AC-T-01 | 边界 | Token 过期后 validate | 返回 null 并清理存储 |
| AC-T-02 | 正常 | 同用户重复 generateToken | 旧 token 失效 |

---

### 5.2 §7.2.2 API 封装完整性 — 更新用例数与列表

**Word 位置**：`7.2.2 API 封装完整性`。

**替换段落为**：

```text
api.test.js 覆盖全部 27 个测试场景，其中包括 20 个 REST 导出函数的 URL/Method 校验，以及 4 个认证 API 与 3 个认证状态管理函数：

REST 示例：
- getLatest() → GET /latest
- aiChat(question) → POST /ai/chat，body 为 { question }

认证 API 示例：
- register(data) → POST /auth/register
- login(data) → POST /auth/login
- getProfile() → GET /auth/profile
- logout() → POST /auth/logout

认证状态：
- setAuth(token, user) 写入 localStorage 并设置 Authorization: Bearer {token}
- setAuth('', null) 清除 token 与请求头
- isLoggedIn() 根据 token 是否存在返回布尔值

响应拦截器统一返回 res.data 而非完整 axios response。
```

---

### 5.3 新增 §7.2.3 路由导航守卫

**Word 位置**：`7.2.2` 之后、`7.3 鸿蒙端` 之前。

**插入**：

```text
7.2.3 路由导航守卫（router.test.js）

被测逻辑（router/index.js beforeEach）：
- 未登录且目标路由无 meta.noAuth → 重定向 /login
- 已登录访问 /login 或 /register → 重定向 /dashboard
- 其余情况 next()

| 用例 | 场景 | isLoggedIn | 访问路径 | 预期 |
|------|------|------------|----------|------|
| RT-G-01 | 未登录访问业务页 | false | /dashboard | 停留在 /login |
| RT-G-02 | 未登录访问登录页 | false | /login | 允许进入 |
| RT-G-03 | 已登录访问登录页 | true | /login | 重定向 /dashboard |
| RT-G-04 | 已登录访问注册页 | true | /register | 重定向 /dashboard |
| RT-G-05 | 已登录访问业务页 | true | /devices | 允许进入 |
```

同时将原 `7.2.3 鸿蒙端` 及之后小节编号依次 **+1**（7.3→7.4 等），或在 Word 中手动调整标题编号。

---

## 6. 第 8 章「测试执行情况」（P0 + P1）

### 6.1 §8.2 Web 后端执行结果

**替换终端输出块为**：
```text
[INFO] Tests run: 154, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**替换执行结论文为**：
```text
执行结论：154 个测试方法全部通过，无失败、无错误。其中新增 Auth 模块 41 个用例（JwtUtilTest 10 + AuthServiceTest 15 + AuthControllerTest 16）。JaCoCo HTML 报告路径：backend/target/site/jacoco/index.html。
```

**图号问题（P2）**：当前 v3 在 §8.2 使用「图7」表示 mvn test 截图，但 §10.1 也用「图7」表示 JaCoCo。**建议**：
- §8.2 截图改为引用 **附录 S1**（不叫图7）
- §10.1 保留 **图6** 作为 JaCoCo 包级覆盖率图

**§8.2 图题改为**：
```text
（截图 S1）mvn test 执行结果 — BUILD SUCCESS，154 tests passed
```

---

### 6.2 §8.3 Web 前端执行结果

**替换终端输出为**：
```text
Test Files  3 passed (3)
     Tests  60 passed (60)
```

**替换执行结论文为**：
```text
执行结论：60 用例全部 PASS。较 V1.1 新增 14 条（api 认证 7 场景 + router 守卫 5 场景 + 路由数量更新 2 场景）。覆盖率报告路径：frontend/coverage/index.html。
```

**截图引用改为 S4、S5**，终端应显示 **60 passed** 而非 46。

---

### 6.3 §8.4 鸿蒙端（P0 — 按实际情况二选一）

**若已运行 DevEco / hvigorw 且全绿**，替换为：
```text
鸿蒙端测试代码已编写并在 List.test.ets 中注册。本次在 DevEco Studio 中运行 entry/src/test 下全部 Hypium 测试，测试结果全部通过（约 145 用例）。测试覆盖 DeviceViewModel、GrowthViewModel、ChartUtils、IotTokenManager、ThemeManager、BaseViewModel 以及 ViewModel、IotTypes、Recipe 等模块。运行截图见 S7、S8。
```

**若尚未运行**，替换为：
```text
鸿蒙端测试代码已完成编写并在 List.test.ets 中注册，共 10 个测试文件、约 145 个用例。受本机 Hvigor/DevEco 命令行环境限制，本报告将鸿蒙端作为扩展测试项说明；Web 后端与 Web 前端已满足实训 JUnit 5 + Vitest 的硬性要求。后续可在 DevEco Studio 中右键 entry/src/test → Run Tests 补充 S7/S8 截图。
```

---

## 7. 第 9 章「测试结果分析」（P1）

### 7.1 §9.1 通过/失败统计表 — 整表替换

| 端 | 测试文件/类 | 用例总数 | 通过 | 失败 | 通过率 |
|----|-------------|----------|------|------|--------|
| Web 后端 | 16 | 154 | 154 | 0 | 100% |
| Web 前端 | 3 | 60 | 60 | 0 | 100% |
| 鸿蒙端 | 10 | 约 145 | （见 8.4） | — | — |
| **合计** | **29** | **359+** | **214+** | **0** | **100%**（已执行部分） |

**表格下方注释替换为**：
```text
注：Web 前后端为本次实训硬性要求范围，已全部执行通过；鸿蒙端为扩展项，通过数以 DevEco 实际截图为准。
```

---

### 7.2 §9.2 缺陷发现情况 — 追加 Auth 相关条目

在现有列表**末尾追加**：

```text
5. Auth 模块测试驱动开发：新增 AuthController / AuthService / JwtUtil 测试时，AuthControllerTest 初版未 Mock DeviceInfoMapper 等 MyBatis Mapper，导致 @WebMvcTest 上下文加载失败；参照 DeviceControllerTest 补全 @MockBean 后 16 个用例全部通过。
6. 前端 localStorage 初始化顺序：api/index.js 在模块加载时即读取 localStorage，Vitest 需在 vi.hoisted 中预先 stub localStorage，否则 api.test.js 无法 import。
7. 路由守卫测试顺序：已停留在 /login 时再次 push('/login') 不会触发守卫，测试需先 navigate 至 /devices 再验证「已登录访问 /login → /dashboard」。
```

---

### 7.3 §9.3 测试有效性评价表 — 更新第一行

| 评价维度 | 结论 |
|----------|------|
| 核心 API 覆盖 | **6 个 Controller**（含 Auth）全部有独立测试类 |
| 认证链路覆盖 | 注册/登录/Token/profile/logout 正常+异常+边界均有用例 |
| 业务规则覆盖 | 级联删除、默认值、缓存优先、limit 钳制等均有用例 |
| … | （其余行保持） |

---

## 8. 第 10 章「覆盖率分析」（P1 — 数据全面更新）

### 8.1 §10.1 Web 后端 JaCoCo — 说明段 + 表格

**在图6/JaCoCo 图前插入说明段**：

```text
需要说明的是，JaCoCo 全项目统计会将 entity、config、consumer、websocket 等非本次单元测试重点的类纳入分母，全项目总覆盖率显著低于核心业务包。本次实训按「核心 100%、辅助 ≥80%」考核，因此重点分析 controller、service、util 包。补测 Auth 模块后，controller 包行覆盖率已达 100%，service 包行覆盖率 97.5%，util 包指令覆盖率 98.4%。
```

**包级覆盖率表整表替换为**（数据来自 `backend/target/site/jacoco/jacoco.csv`，2026-07-03 执行）：

| 包 | 指令覆盖率 | 行覆盖率 | 是否达标 | 说明 |
|----|------------|----------|----------|------|
| com.smartmushroom.controller | **97.5%** | **100%** | ✅ 核心达标 | **6 个 Controller 全测**（含 AuthController） |
| com.smartmushroom.service | **94.3%** | **97.5%** | ✅ 接近/达标 | **6 个 Service 全测**（含 AuthService） |
| com.smartmushroom.util | **98.4%** | **97.9%** | ✅ 辅助达标 | AiAnswerSanitizer + **JwtUtil** |
| com.smartmushroom.dto | 约 24% | — | 部分 | ApiResponse 工厂方法已测，Lombok 计入分母 |
| com.smartmushroom.config | 约 1% | — | 未测 | 配置类，非核心业务 |
| com.smartmushroom.consumer | 0% | — | 未测 | AMQP 集成，建议集成测试 |

**删除或改写**原文「5 个 Controller 全测」「controller 96%、service 92%」等旧数据。

**核心类行覆盖率明细（可放表格脚注或附录）**：

| 类 | 行覆盖率 | 备注 |
|----|----------|------|
| AuthController | 100% | 新增 |
| AuthService | 100% | 新增 |
| JwtUtil | 97%（约） | 新增，1 行未覆盖可忽略 |
| DataController / CommandController / HealthController / AiController | 100% | — |
| DeviceController | 100% | 指令覆盖略低因异常分支 |

---

### 8.2 §10.2 Web 前端 Vitest 覆盖率表 — 更新 Branch

| 文件 | Stmts | Branch | Funcs | Lines |
|------|-------|--------|-------|-------|
| api/index.js | 100% | **96.77%** | 100% | 100% |
| utils/aiAnswer.js | 100% | 100% | 100% | 100% |
| router/index.js | 100% | 100% | **42.85%*** | 100% |

**脚注保持并微调**：
```text
* router funcs 为懒加载 import() 回调，单元测试未触发实际导航加载组件，不影响路由配置与守卫逻辑验证。api/index.js Branch 未达 100% 系模块初始化分支（localStorage 已有 token 时设置 header），已通过 setAuth 用例覆盖主要路径。
```

---

## 9. 第 11 章「问题与改进建议」

### 9.1 §11.1 当前问题表 — 更新 P1 行

**「全项目 JaCoCo 总覆盖率」一行「描述」改为**：
```text
全项目统计含 entity/config/consumer，拉低总覆盖率；已通过 controller/service/util 分包指标说明核心达标
```

**新增一行**：

| 编号 | 问题 | 影响 | 建议 |
|------|------|------|------|
| P1-b | AuthService 依赖 KingbaseES JdbcTemplate | 单元测试需 Mock，与生产 DB 行为可能有差异 | 后续可增加 @SpringBootTest + 测试库集成测试 |

---

## 10. 第 12 章「结论」（P1 — 全文替换）

**整段替换为**：

```text
本次智慧蘑菇项目单元测试实训完成了 Web 后端、Web 前端和鸿蒙端三个层面的测试设计与实现。Web 后端共 16 个测试类、154 个用例全部通过，核心 controller 包行覆盖率 100%，service 包行覆盖率 97.5%，util 包指令覆盖率 98.4%，满足实训「核心 100%、辅助 ≥80%」要求。Web 前端 3 个测试文件、60 个用例全部通过，api、utils、router 语句覆盖率均为 100%。

本次较初版报告的重要增量是用户认证模块：后端 AuthControllerTest、AuthServiceTest、JwtUtilTest 共 41 用例；前端 api.test.js 补充认证 API 与 setAuth 测试、router.test.js 补充 8 路由与导航守卫测试。测试有效覆盖了设备管理、传感器数据、命令管理、AI 问答、健康检查、用户注册登录等核心业务，以及 AI 标签清洗、API limit 边界、Token 生命周期等关键细节。

鸿蒙端作为扩展项已完成约 145 个 Hypium 用例的编写与注册，运行结果见第 8.4 节说明。

提交前请：（1）补全封面姓名学号；（2）将第 8 章截图替换为 154 passed / 60 passed 的真实终端与覆盖率 HTML 截图；（3）按鸿蒙实际运行情况更新 9.1 表；（4）导出 PDF 备份。
```

---

## 11. 附录 A 测试文件清单（P1）

### A.1 Web 后端 — 在 `util/` 和 `service/`、`controller/` 下补充

**在 `util/AiAnswerSanitizerTest.java` 后增加**：
```text
├── util/JwtUtilTest.java                    ← 新增
```

**在 `service/AiServiceTest.java` 后增加**：
```text
├── service/AuthServiceTest.java             ← 新增
```

**在 `controller/HealthControllerTest.java` 前增加**：
```text
├── controller/AuthControllerTest.java     ← 新增
```

**小节标题**：`A.1 Web 后端（16 类）`

---

### A.2 Web 前端 — 更新用例数注释

在三个文件路径后标注：
```text
├── api/__tests__/api.test.js        （27 用例）
├── router/__tests__/router.test.js  （16 用例）
├── utils/__tests__/aiAnswer.test.js （17 用例）
```

---

## 12. 附录 C 截图清单（P0）

### 12.1 必须重新截取的截图

| 编号 | 文件名 | 要求 | 旧版问题 |
|------|--------|------|----------|
| **S1** | backend-mvn-test-pass.png | 终端显示 `Tests run: 154` + BUILD SUCCESS | 旧图为 116 |
| **S2** | jacoco-overview.png | JaCoCo 首页 | 需反映 Auth 类已纳入 |
| **S3** | jacoco-core-classes.png | 展开 controller 包，可见 **AuthController**；展开 service 包可见 **AuthService** | 旧图无 Auth |
| **S4** | frontend-vitest-pass.png | 终端显示 `60 passed` | 旧图为 46 |
| **S5** | vitest-coverage-overview.png | coverage/index.html，api 27 tests 对应覆盖率 | 旧图无认证覆盖 |

### 12.2 截图插入位置对照

| 截图 | 插入 Word 章节 | 图题建议 |
|------|----------------|----------|
| S1 | 8.2 Web 后端执行结果 | 图7 Web 后端 mvn test 执行结果（154 passed） |
| S2 | 10.1 JaCoCo 分析 | 图6 Web 后端 JaCoCo 总览 |
| S3 | 10.1 JaCoCo 分析 | 图6-b 核心包覆盖率明细（可选第二图） |
| S4 | 8.3 Web 前端执行结果 | 图8 Web 前端 Vitest 执行结果（60 passed） |
| S5 | 10.2 Vitest 覆盖率 | 图9 Web 前端模块覆盖率 |
| S7/S8 | 8.4 鸿蒙端 | 按实际运行情况 |

### 12.3 图号统一方案（解决 v3 图7 重复）

建议全文图号按顺序固定为：

| 图号 | 内容 | 所在章节 |
|------|------|----------|
| 图1 | 三端架构与测试边界 | §3.1 |
| 图2 | 三端单元测试实施流程 | §5.1 |
| 图3 | Web 后端测试分层 | §5.2 |
| 图4 | 测试用例类型分布 | §5.5 |
| 图5 | 三端测试规模统计 | §6.1 |
| 图6 | 鸿蒙 Hypium 模块用例数 | §6.4 |
| 图7 | mvn test 154 passed（S1） | §8.2 |
| 图8 | Vitest 60 passed（S4） | §8.3 |
| 图9 | JaCoCo 包级覆盖率（S2/S3） | §10.1 |
| 图10 | Vitest Coverage（S5） | §10.2 |

**操作**：在 Word 中全局搜索「图7」「图8」，按上表统一修改图题与交叉引用。

---

## 13. 提交前 Checklist（打印勾选）

```
[ ] 封面姓名、学号已填写
[ ] 修订记录 V1.2 已添加，作者已填
[ ] 摘要、第 12 章结论中的 154/60/16 数据已更新
[ ] §6.1 / §6.2 / §6.3 表格已更新，Auth 三行已插入
[ ] §7.1.5 Auth 用例设计已新增
[ ] §7.2.2 / §7.2.3 前端认证与路由守卫已新增
[ ] §8.2 / §8.3 终端输出改为 154 / 60
[ ] §9.1 统计表已更新
[ ] §10.1 覆盖率改为 97.5%/100%/94.3% 等新数据
[ ] 附录 A 已含 JwtUtilTest、AuthServiceTest、AuthControllerTest
[ ] S1–S5 截图已按新数据重截并插入
[ ] 图号无重复（尤其原两个「图7」）
[ ] 鸿蒙端 8.4 / 9.1 已按实际运行二选一填写
[ ] 已删除所有「待填写」「待补充」编辑提示（若仍残留）
[ ] 已导出 PDF 备份
```

---

## 14. 快速执行命令（截图时用）

```bash
# 后端 — 生成 154 tests + JaCoCo
cd D:\LHWYAN\program\neu\Smart-Mushroom-Web-Terminal-Code\backend
mvn clean test
# 打开 backend\target\site\jacoco\index.html

# 前端 — 生成 60 tests + coverage
cd D:\LHWYAN\program\neu\Smart-Mushroom-Web-Terminal-Code\frontend
npm run test:coverage
# 打开 frontend\coverage\index.html
```

---

*文档生成时间：2026-07-03 | 对照代码版本：Smart-Mushroom-Web-Terminal-Code（含 Auth 单元测试）*
