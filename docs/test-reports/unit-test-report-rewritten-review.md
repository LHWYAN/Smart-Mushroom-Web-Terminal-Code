# `unit-test-report-rewritten.docx` 审阅与修改建议

审阅对象：`C:\Users\18526\Downloads\unit-test-report-rewritten.docx`

审阅时间：2026-07-03

## 1. 总体结论

这份 Word 报告的**主体内容已经比较全面**，基本覆盖了单元测试实训报告应有的核心部分：

- 测试背景、测试目的、测试依据；
- Web 后端、Web 前端、鸿蒙端三端测试范围；
- JUnit 5、Vitest、Hypium、JaCoCo 等工具说明；
- 测试策略、测试对象、代表性测试用例；
- Web 后端与 Web 前端的执行结果；
- 覆盖率分析、问题与改进建议；
- 附录中的测试文件清单、运行命令、截图清单。

从文档结构看，它已经比普通实训报告更完整，具备拿高分的基础。不过，当前文档还**不建议直接提交**，因为仍存在以下几类问题：

1. 封面和修订记录仍有 `【待填写】` 内容。
2. 鸿蒙端测试结果还没有真实运行截图或最终通过数量。
3. Web 后端和前端虽然有文字结果，但建议补充真实终端截图和覆盖率 HTML 截图。
4. 个别图号、章节引用和“图几”的编号需要人工核对，避免图文不一致。
5. 覆盖率表述需要更严谨地区分“全项目覆盖率”和“核心业务包覆盖率”。
6. 如果最终提交 Word，需要检查图片分页、表格跨页和目录页码。

下面按“必须修改”“建议增强”“可选优化”三个等级列出具体修改方法。

---

## 2. 必须修改项

### 2.1 填写封面中的个人信息

**问题位置：**

文档封面表格中仍有：

- `学生姓名 | 【待填写：请填写真实姓名】`
- `学号 | 【待填写：请填写真实学号】`

**为什么必须改：**

这是正式实训报告的基本信息，如果保留 `【待填写】`，会明显像未完成稿。

**如何修改：**

在 Word 封面表格中直接替换：

```text
学生姓名：你的真实姓名
学号：你的真实学号
```

如果不想透露姓名给我，可以你自己在 Word 中手动修改。

---

### 2.2 修订记录中的作者也要填写

**问题位置：**

修订记录表中有：

```text
V1.0 | 2026-07-03 | 形成三端单元测试报告初稿 | 【待填写】
V1.1 | 2026-07-03 | 重写报告语言，统一字体与版式，替换模糊图片，增加人工补充提示 | 【待填写】
```

**如何修改：**

把作者列改成你的姓名即可：

```text
V1.0 | 2026-07-03 | 形成三端单元测试报告初稿 | 你的姓名
V1.1 | 2026-07-03 | 重写报告语言，统一字体与版式，替换模糊图片，增加人工补充提示 | 你的姓名
```

---

### 2.3 删除或处理所有 `【待补充】` / `【提交前必须处理】` 提示

**问题位置：**

文档中有一张“人工补充项总览”表，内容包括：

```text
【提交前必须处理】
【待补充项总览】
```

这些提示对你编辑报告很有帮助，但**正式提交版中不应该保留太多编辑提示**。

**如何修改：**

建议保留一小段“说明”即可，不要保留醒目的待处理提示。

可替换为：

```text
说明：本报告基于当前项目单元测试结果编写，Web 后端和 Web 前端测试已完成并通过；鸿蒙端测试代码已完成，运行结果以 DevEco Studio 或 hvigorw 实际截图为准。
```

如果你已经补齐所有真实截图，可以进一步改成：

```text
说明：本报告基于当前项目单元测试结果编写，Web 后端、Web 前端与鸿蒙端测试均已完成，相关运行结果与覆盖率截图已插入正文。
```

---

### 2.4 鸿蒙端测试结果不能只写“已编写”，需要补运行证据

**当前文档表述：**

文档中写到：

```text
鸿蒙端已完成 10 个 Hypium 测试文件、约 145 个用例的编写与注册，最终通过截图需在 DevEco Studio 或 hvigorw 环境中补充。
```

这个表述是诚实的，但如果目标是高分，最好补上真实运行结果。

**为什么重要：**

鸿蒙端是报告的加分项。如果只说“已编写与注册”，但没有运行结果截图，老师可能会认为它只是计划或代码补充，而不是完整测试闭环。

**如何修改：**

在 DevEco Studio 中打开：

```text
D:\LHWYAN\program\neu\Smart-Mushroom-HarmonyOS-Terminal-Code
```

然后运行：

```text
entry/src/test
```

或在项目根目录运行：

```bash
hvigorw test
```

运行后截图：

- DevEco Studio 测试树全绿；
- 或命令行中显示测试通过的输出。

把截图插入第 `8.4 鸿蒙端执行结果` 小节。

然后把原文：

```text
鸿蒙端测试代码已编写并在 List.test.ets 中注册。由于 Hypium 命令行运行依赖 DevEco Studio / Hvigor 环境，最终提交前应在本机环境执行一次并补充真实结果截图。
```

改成：

```text
鸿蒙端测试代码已编写并在 List.test.ets 中注册。本次在 DevEco Studio 中运行 entry/src/test 下全部 Hypium 测试，测试结果全部通过。测试覆盖 DeviceViewModel、GrowthViewModel、ChartUtils、IotTokenManager、ThemeManager、BaseViewModel 以及既有 ViewModel、IotTypes、Recipe 等模块。
```

如果最终确实没来得及跑鸿蒙测试，则建议保守写成：

```text
鸿蒙端测试代码已完成编写并在 List.test.ets 中注册。受本机 Hvigor/DevEco 命令行环境限制，本报告将鸿蒙端作为扩展测试内容进行说明；后续可在 DevEco Studio 中运行 entry/src/test 获取完整执行截图。
```

不要写“全部通过”，除非你真的跑过并截图。

---

### 2.5 通过/失败统计表中的鸿蒙端数据需要改成最终值

**问题位置：**

第 `9.1 通过/失败统计` 表中当前类似：

```text
鸿蒙端 | 10 | 约 145 | 【待补充】 | 【待补充】 | 【待补充】
```

**如何修改：**

如果鸿蒙端运行通过，改成：

```text
鸿蒙端 | 10 | 约 145 | 约 145 | 0 | 100%
```

如果 DevEco Studio 显示了准确用例数量，比如 145，则写准确数量：

```text
鸿蒙端 | 10 | 145 | 145 | 0 | 100%
```

如果无法运行，建议改成：

```text
鸿蒙端 | 10 | 约 145 | 未在当前环境执行 | - | -
```

并在表格下方加一句说明：

```text
注：鸿蒙端测试代码已完成编写与注册，因运行依赖 DevEco Studio / Hvigor 环境，最终通过数量以本机运行截图为准。
```

---

## 3. 建议增强项

### 3.1 Web 后端建议插入真实 JaCoCo 截图

**当前情况：**

文档已经写明：

```text
Web 后端 116 个测试方法全部通过
Controller 包 96%，Service 包 92%，Util 包 100%
```

内容是正确且有说服力的，但最好插入真实截图。

**建议截图：**

运行：

```bash
cd D:\LHWYAN\program\neu\Smart-Mushroom-Web-Terminal-Code\backend
mvn test
```

截图 1：

```text
[INFO] Tests run: 116, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

截图 2：

打开：

```text
backend/target/site/jacoco/index.html
```

截图 JaCoCo 总览页。

截图 3：

打开 `com.smartmushroom.controller` 和 `com.smartmushroom.service` 包详情页，截图核心包覆盖率。

**插入位置：**

- `8.2 Web 后端执行结果`
- `10.1 Web 后端 JaCoCo 包级覆盖率`

**推荐替换文字：**

```text
从 JaCoCo 报告可以看出，虽然 config、consumer、websocket 等基础设施包未作为本次单元测试重点，导致全项目总覆盖率受到影响，但核心业务包覆盖率较高。其中 controller 包指令覆盖率为 96%，service 包指令覆盖率为 92%，util 包达到 100%，能够较好支撑本次实训对核心功能的覆盖要求。
```

---

### 3.2 Web 前端建议插入真实 Vitest Coverage 截图

**当前情况：**

文档已经写明：

```text
Test Files 3 passed (3)
Tests 46 passed (46)
api / utils / router 覆盖率 100%
```

这个结果很好，但建议插入真实截图。

**运行命令：**

```bash
cd D:\LHWYAN\program\neu\Smart-Mushroom-Web-Terminal-Code\frontend
npm run test:coverage
```

**建议截图：**

1. 终端中 `46 passed` 的结果。
2. `frontend/coverage/index.html` 的 HTML 覆盖率总览。

**插入位置：**

- `8.3 Web 前端执行结果`
- `10.2 Web 前端 Vitest 覆盖率`

---

### 3.3 图号需要统一核对

**发现的问题：**

从提取内容看，正文中图号出现过：

- 图1：系统三端架构；
- 图2：三端单元测试实施流程；
- 图3：Web 后端单元测试分层策略；
- 图4：测试用例类型分布；
- 图5：三端测试规模统计；
- 图6：Web 后端 JaCoCo 包级覆盖率；
- 图7：Web 前端 Vitest 模块覆盖率；
- 图8：鸿蒙端 Hypium 各模块用例数量。

这个编号体系整体可以，但需要确保 Word 中每张图下方的说明和插入图片一致。

**如何检查：**

逐张看 Word：

| 图号 | 应该显示的图片 | 检查点 |
|------|----------------|--------|
| 图1 | 系统三端架构图 | 是否含 Web 前端、Web 后端、鸿蒙端 |
| 图2 | 实施流程图 | 是否是“环境搭建 -> 用例设计 -> 执行测试”等流程 |
| 图3 | 后端分层图 | 是否是 Controller / Service / Util 分层 |
| 图4 | 用例类型分布图 | 是否是正常 / 异常 / 边界场景 |
| 图5 | 三端测试规模统计 | 是否展示后端、前端、鸿蒙端数量 |
| 图6 | 后端覆盖率图 | 是否展示 Controller 96%、Service 92%、Util 100% |
| 图7 | 前端覆盖率图 | 是否展示 api、utils、router 100% |
| 图8 | 鸿蒙模块用例图 | 是否展示 ViewModel、IotTypes、Recipe 等模块 |

**如果不一致：**

直接修改图题或替换图片。图题建议统一格式：

```text
图1  智慧蘑菇系统三端架构与单元测试边界
图2  三端单元测试实施流程
图3  Web 后端单元测试分层策略
图4  三端测试用例类型分布
图5  三端测试规模统计
图6  Web 后端 JaCoCo 包级覆盖率
图7  Web 前端 Vitest 模块覆盖率
图8  鸿蒙端 Hypium 各模块用例数量
```

---

### 3.4 覆盖率目标表述建议更严谨

**当前风险：**

报告中写了“核心功能尽量接近 100%”，但实训文件里写的是“核心功能必须达到 100% 覆盖”。而 JaCoCo 结果中：

```text
Controller 包 96%
Service 包 92%
Util 包 100%
全项目总覆盖率约 33%
```

老师如果只看“100%”目标，可能会问为什么不是 100%。

**建议解释方式：**

在 `10.1 Web 后端 JaCoCo 包级覆盖率` 后补充一段：

```text
需要说明的是，JaCoCo 的全项目统计包含了 entity、config、consumer、websocket 等并非本次单元测试重点的类，其中 Lombok 生成方法、配置类以及 AMQP/WebSocket 集成组件会显著拉低全项目总覆盖率。本次实训的重点是核心业务逻辑，因此报告中重点分析 controller、service、util 等核心包。后续若需要进一步提高全项目覆盖率，可以通过 JaCoCo includes/excludes 精确限定统计范围，或补充集成测试覆盖 AMQP 与 WebSocket 链路。
```

这样写更专业，也能解释“全局 33%”和“核心包高覆盖”的差异。

---

### 3.5 “缺陷发现情况”可以增加一个更真实的测试修正案例

**当前文档已有：**

```text
鸿蒙测试需严格与源码 API 对齐，初版测试中出现的参数和字段不一致问题已修正。
```

建议展开一点，会更像真实测试过程。

**建议添加：**

```text
在鸿蒙端测试编写过程中，初版 GrowthViewModel.test.ets 曾误将 addBatch 写成三个字符串参数，而实际源码签名为 addBatch(batch: GrowthBatch)。通过核对源码和测试编译检查，最终改为构造完整 GrowthBatch 对象后再调用 addBatch。类似地，DeviceViewModel.test.ets 中曾误用 deviceType/deviceId 字段，后根据 DeviceItem 模型修正为 type/id 字段。这说明单元测试不仅验证生产代码，也能反向暴露测试代码与业务模型不一致的问题。
```

这段可以放在：

```text
9.2 缺陷发现情况
```

---

## 4. 可选优化项

### 4.1 目录可以改成 Word 自动目录

当前目录是普通文本表格或手写目录。如果最终提交 Word，建议使用 Word 的：

```text
引用 -> 目录 -> 自动目录
```

这样页码更正式。

**如何做：**

1. 删除当前手写目录；
2. 确认所有章节标题使用 Word 标题样式；
3. 插入自动目录；
4. 右键目录 -> 更新域。

如果时间不够，保留当前目录也可以，不是硬伤。

---

### 4.2 建议导出 PDF 备份

如果提交系统允许，建议同时保留：

```text
unit-test-report-rewritten.docx
unit-test-report-rewritten.pdf
```

PDF 可以避免 Word 版本不同导致图片错位。

---

### 4.3 图片如果不够清晰，优先替换这几张

从文档结构看，已包含 8 张图片。若你觉得图片质量一般，优先替换以下几类：

| 优先级 | 图片 | 替换来源 |
|--------|------|----------|
| 高 | 后端覆盖率图 | 真实 JaCoCo HTML 截图 |
| 高 | 前端覆盖率图 | 真实 Vitest Coverage HTML 截图 |
| 高 | 测试执行截图 | 终端 BUILD SUCCESS / 46 passed |
| 中 | 鸿蒙测试图 | DevEco Studio 测试树全绿 |
| 低 | 架构图、流程图 | 当前生成图已经基本够用 |

---

## 5. 建议最终提交前逐项检查

请按下面清单最终检查：

- [ ] 封面姓名已填写。
- [ ] 封面学号已填写。
- [ ] 修订记录作者已填写。
- [ ] 删除或处理了所有 `【待填写】`。
- [ ] 删除或处理了所有 `【待补充】`。
- [ ] Web 后端 `mvn test` 截图已插入。
- [ ] JaCoCo 首页截图已插入。
- [ ] JaCoCo Controller / Service 包详情截图已插入。
- [ ] Web 前端 `npm run test:coverage` 截图已插入。
- [ ] Vitest Coverage HTML 截图已插入。
- [ ] 鸿蒙端 DevEco Studio 或 `hvigorw test` 通过截图已插入，或已明确说明未在当前环境执行。
- [ ] `9.1 通过/失败统计` 中鸿蒙端数据已更新。
- [ ] 图1 到图8 的图片内容和图题一致。
- [ ] 表格没有严重跨页错位。
- [ ] 如果提交 PDF，已从最终 Word 导出并检查排版。

---

## 6. 推荐修改后的关键文字

如果你想快速修改，不想逐段重写，可以直接把下面几段复制到对应章节。

### 6.1 放到摘要末尾

```text
需要说明的是，Web 后端和 Web 前端测试已在当前环境中完成执行并全部通过；鸿蒙端测试作为扩展内容已完成测试代码编写与入口注册，最终运行结果建议以 DevEco Studio 或 hvigorw 的真实截图补充。报告中的覆盖率分析重点关注 controller、service、util、api、utils、router 等核心业务模块。
```

### 6.2 放到 8.4 鸿蒙端执行结果

如果你已经运行通过：

```text
鸿蒙端测试代码已编写并在 List.test.ets 中注册。本次在 DevEco Studio 中运行 entry/src/test 下全部 Hypium 测试，测试结果全部通过。测试覆盖 DeviceViewModel、GrowthViewModel、ChartUtils、IotTokenManager、ThemeManager、BaseViewModel 以及既有 ViewModel、IotTypes、Recipe 等模块。相关运行截图见本节下方。
```

如果你还没运行：

```text
鸿蒙端测试代码已完成编写并在 List.test.ets 中注册。受本机 Hvigor/DevEco 命令行环境限制，本报告将鸿蒙端作为扩展测试内容进行说明；后续可在 DevEco Studio 中运行 entry/src/test 获取完整执行截图。该部分不影响 Web 后端与 Web 前端对实训基本要求的满足。
```

### 6.3 放到 10.1 覆盖率分析

```text
JaCoCo 全项目统计会将 entity、config、consumer、websocket 等非本次单元测试重点的类纳入分母，因此全项目总覆盖率低于核心业务包覆盖率。本次实训重点是核心业务逻辑的单元测试，所以报告重点分析 controller、service、util 等包。其中 controller 包达到 96%，service 包达到 92%，util 包达到 100%，能够较好说明核心功能已得到充分验证。
```

### 6.4 放到 9.2 缺陷发现情况

```text
在鸿蒙端测试编写过程中，初版 GrowthViewModel.test.ets 曾误将 addBatch 写成三个字符串参数，而实际源码签名为 addBatch(batch: GrowthBatch)。通过核对源码和测试编译检查，最终改为构造完整 GrowthBatch 对象后再调用 addBatch。类似地，DeviceViewModel.test.ets 中曾误用 deviceType/deviceId 字段，后根据 DeviceItem 模型修正为 type/id 字段。这说明单元测试不仅验证生产代码，也能反向暴露测试代码与业务模型不一致的问题。
```

---

## 7. 最终评价

综合来看，`unit-test-report-rewritten.docx` 的内容**已经足够全面**，比普通实训报告更加完整，三端测试、用例规模、工具链和覆盖率分析都有体现。

但它目前还属于“高质量待完善版”，不是“最终可直接提交版”。只要完成以下三件事，就可以作为较高质量的最终报告提交：

1. 填写个人信息，删除所有待填写/待补充提示。
2. 插入 Web 后端、Web 前端、鸿蒙端的真实运行截图。
3. 更新鸿蒙端通过数量和图号一致性。

完成后，这份报告整体质量会比较高，能够清楚体现你不仅完成了 JUnit 5 和 Vitest 的基本要求，还额外补充了鸿蒙端 Hypium 测试，对实训成绩是有帮助的。
