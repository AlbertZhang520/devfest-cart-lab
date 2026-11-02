# DevFest Cart Lab — Agent 工程指南 (AGENTS.md)

本仓库是 **DevFest 2026 Workshop：Android Agentic Engineering** 的动手实验工程（`devfest-cart-lab`）。
所有进入本工程的 Coding Agent（Gemini CLI / Claude Code / OpenCode / Cursor）必须严格遵守以下架构规范与验证护栏。

## 1. 工程结构与核心模块

- `app/src/main/kotlin/com/example/devfestcartlab/`
  - `domain/CartPriceCalculator.kt`：购物车结算核心算价器（纯 Kotlin 领域层，无 Android 依赖）
  - `ui/checkout/CheckoutViewModel.kt`：购物车结算页状态管理（`StateFlow<CheckoutUiState>`）
  - `ui/checkout/CheckoutScreen.kt`：购物车结算页 Jetpack Compose 界面
  - `MainActivity.kt`：应用入口 Activity（已开启 `enableEdgeToEdge()`）
- `app/src/test/kotlin/com/example/devfestcartlab/`
  - `domain/CartPriceCalculatorTest.kt`：结算价格计算验收单元测试（**只读验收靶子**）
- `skills/`（通过 `android skills add` 挂载的官方技能库）
  - `skills/android-cli/SKILL.md`：Android CLI 构建、安装、布局树检查（`android layout`）与截图标注指南
  - `skills/edge-to-edge/SKILL.md`：Jetpack Compose Edge-to-Edge 与 `WindowInsets` 适配规范
  - `skills/testing-setup/SKILL.md`：Android 分层测试策略规范

## 2. 购物车算价业务契约 (`CartPriceCalculator`)

计算购物车应付金额 `calculate(items, rule)` 必须严格遵循以下顺序与边界约束：

1. **商品原价小计 (`subtotal`)**：`items.sumOf { it.unitPrice * it.quantity }`（保留两位小数）。
2. **先算满额折扣 (`discountedSubtotal`)**：
   - 满减门槛必须基于**商品原价小计 (`subtotal`)** 判定，而非扣除优惠券后的金额。
   - 当 `subtotal >= rule.threshold` 时，`discountedSubtotal = roundCurrency(subtotal * rule.discountRate)`；否则 `discountedSubtotal = subtotal`。
3. **后扣优惠券 + 0 元下限兜底 (`finalPayable`)**：
   - 在 `discountedSubtotal` 基础上减去 `rule.couponAmount`。
   - **非负兜底红线**：当优惠券面额大于折后金额时，`finalPayable` 必须以 `0.0` 兜底（`coerceAtLeast(0.0)`），严禁出现负数应付金额。

## 3. UI 适配契约 (`CheckoutScreen` & Edge-to-Edge)

修改 `CheckoutScreen.kt` 时，必须先阅读 `skills/edge-to-edge/SKILL.md`：

1. 优先使用 Material 3 `Scaffold`，将底部「立即结算」栏放入 `bottomBar` 槽位（或为底部栏应用 `Modifier.navigationBarsPadding()` / `WindowInsets.safeDrawing`），确保底部按钮不与系统导航栏重叠。
2. 将 `Scaffold` 的 `innerPadding` 传递给 `LazyColumn` 的 `contentPadding` 并调用 `.consumeWindowInsets(innerPadding)`，避免顶栏或底部栏遮挡列表首尾项。
3. 页面内包含 `OutlinedTextField`（订单备注），需按照 `skills/edge-to-edge/SKILL.md` 的 IME 规范添加 `Modifier.imePadding()` 或 `Modifier.fitInside(WindowInsetsRulers.Ime.current)`。

## 4. 验证闭环与硬护栏（Verification Loop）

1. **禁止篡改测试靶子**：**严禁修改或删除 `app/src/test/**` 下的任何测试文件或断言**。单测是唯一的业务验收标准。
2. **测试不过不收工**：
   - 每次修改 `CartPriceCalculator.kt` 后，必须自行在终端执行：
     ```bash
     ./gradlew testDebugUnitTest
     ```
   - 若测试失败，请直接读取终端报错栈与失败断言，定位漏修的边界条件并继续修复，直到 `./gradlew testDebugUnitTest` 输出 `BUILD SUCCESSFUL` 才可结束任务。
3. **UI 与实机验证（如已连接模拟器/真机）**：
   - 编译检查：`./gradlew assembleDebug`
   - 使用 Android CLI 部署与检查 UI 树（详见 `skills/android-cli/SKILL.md`）：
     ```bash
     android run app/build/outputs/apk/debug/app-debug.apk
     android layout --pretty
     android screen capture --annotate -o checkout-screen.png
     ```
