package com.example.devfestcartlab.ui.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.devfestcartlab.ui.theme.BrandBlue
import com.example.devfestcartlab.ui.theme.CoralPrice
import com.example.devfestcartlab.ui.theme.DevFestCartLabTheme

/**
 * GDG DevFest Shanghai 2026 官方周边商城结算页
 *
 * Edge-to-Edge 适配（参见 `skills/edge-to-edge/SKILL.md`）：
 * 1. 底部「立即结算」栏位于 `Scaffold.bottomBar`，内容应用 `navigationBarsPadding()`，背景延伸到系统导航栏下方。
 * 2. `Scaffold` 的 `innerPadding` 顶部传给 `LazyColumn.contentPadding`、底部让列表视口止于结算栏上方，并 `consumeWindowInsets`，首尾项不被遮挡。
 * 3. `Scaffold` 整体应用 `imePadding()`，软键盘弹出时列表与结算栏一起上移，订单备注输入框 (`OutlinedTextField`) 不被遮挡。
 */
@Composable
fun CheckoutScreen(
  modifier: Modifier = Modifier,
  viewModel: CheckoutViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  CheckoutContent(
    uiState = uiState,
    onSelectCoupon = viewModel::selectCoupon,
    onUpdateQuantity = viewModel::updateQuantity,
    onToggleItemSelection = viewModel::toggleItemSelection,
    onToggleSelectAll = viewModel::toggleSelectAll,
    onCycleItemSpec = viewModel::cycleItemSpec,
    onAddCatalogItem = viewModel::addCatalogItem,
    onResetCart = viewModel::resetCart,
    onSelectDeliveryMode = viewModel::selectDeliveryMode,
    onUpdatePromoNote = viewModel::updatePromoNote,
    onAppendQuickNoteTag = viewModel::appendQuickNoteTag,
    onSubmitOrder = viewModel::submitOrder,
    onDismissOrderDialog = viewModel::dismissOrderDialog,
    modifier = modifier,
  )
}

@Composable
internal fun CheckoutContent(
  uiState: CheckoutUiState,
  onSelectCoupon: (String) -> Unit,
  onUpdateQuantity: (String, Int) -> Unit,
  onToggleItemSelection: (String) -> Unit = {},
  onToggleSelectAll: () -> Unit = {},
  onCycleItemSpec: (String) -> Unit = {},
  onAddCatalogItem: (String) -> Unit = {},
  onResetCart: () -> Unit = {},
  onSelectDeliveryMode: (DeliveryMode) -> Unit = {},
  onUpdatePromoNote: (String) -> Unit,
  onAppendQuickNoteTag: (String) -> Unit = {},
  onSubmitOrder: () -> Unit = {},
  onDismissOrderDialog: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  if (uiState.showOrderSubmittedDialog) {
    OrderSubmittedSummaryDialog(
      uiState = uiState,
      onDismiss = onDismissOrderDialog,
    )
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .imePadding(),
    containerColor = MaterialTheme.colorScheme.background,
    bottomBar = {
      CheckoutBottomBar(
        uiState = uiState,
        onToggleSelectAll = onToggleSelectAll,
        onSubmitOrder = onSubmitOrder,
      )
    },
  ) { innerPadding ->
    val layoutDirection = LocalLayoutDirection.current
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = innerPadding.calculateBottomPadding())
        .consumeWindowInsets(innerPadding)
        .testTag("checkout_item_list"),
      contentPadding = PaddingValues(
        start = innerPadding.calculateStartPadding(layoutDirection),
        top = innerPadding.calculateTopPadding(),
        end = innerPadding.calculateEndPadding(layoutDirection),
        bottom = 16.dp,
      ),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      // 1. GDG DevFest Shanghai 商城沉浸式顶栏 + 满 200 打 8 折动态进度条
      item {
        MallHeroBanner(
          subtotal = uiState.breakdown.subtotal,
          threshold = 200.0,
          onResetCart = onResetCart,
        )
      }

      // 2. 上海站会场自提 / 顺丰包邮切换卡片
      item {
        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
          DeliveryAddressCard(
            selectedMode = uiState.deliveryMode,
            onSelectMode = onSelectDeliveryMode,
          )
        }
      }

      // 3. 购物车商品卡片列表
      items(uiState.items, key = { it.id }) { item ->
        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
          MallMerchItemCard(
            item = item,
            isSelected = item.id in uiState.selectedItemIds,
            selectedSpecIndex = uiState.selectedSpecs[item.id] ?: 0,
            onToggleSelect = { onToggleItemSelection(item.id) },
            onCycleSpec = { onCycleItemSpec(item.id) },
            onDecrease = { onUpdateQuantity(item.id, -1) },
            onIncrease = { onUpdateQuantity(item.id, 1) },
          )
        }
      }

      // 4. 上海站人气周边一键凑单横滑区
      if (uiState.addonCandidates.isNotEmpty()) {
        item {
          Box(modifier = Modifier.padding(horizontal = 14.dp)) {
            AddonMerchSection(
              candidates = uiState.addonCandidates,
              onAddItem = onAddCatalogItem,
            )
          }
        }
      }

      // 5. 商城优惠券卡券包选择区
      item {
        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
          CouponTicketSection(
            coupons = uiState.availableCoupons,
            selectedCouponId = uiState.selectedCouponId,
            onSelectCoupon = onSelectCoupon,
          )
        }
      }

      // 6. 订单备注 + 快捷备注标签（用于测试软键盘 IME Inset）
      item {
        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
          OrderNoteCard(
            promoNote = uiState.promoNote,
            onUpdatePromoNote = onUpdatePromoNote,
            onAppendQuickTag = onAppendQuickNoteTag,
          )
        }
      }

      // 7. 结算金额明细小票卡片
      item {
        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
          PriceBreakdownReceiptCard(uiState = uiState)
        }
      }
    }
  }
}

@Composable
private fun CheckoutBottomBar(
  uiState: CheckoutUiState,
  onToggleSelectAll: () -> Unit,
  onSubmitOrder: () -> Unit,
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("checkout_bottom_bar"),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp,
    shadowElevation = 12.dp,
  ) {
    val totalSaved =
      (uiState.breakdown.subtotal - uiState.breakdown.finalPayable).coerceAtLeast(0.0)

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      // 左侧：全选按钮
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(onClick = onToggleSelectAll),
      ) {
        MallCheckCircle(
          checked = uiState.isAllSelected,
          onClick = onToggleSelectAll,
        )
        Text(
          text = "全选",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      // 右侧：合计金额 + 立即结算胶囊按钮
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Column(horizontalAlignment = Alignment.End) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              text = "合计:",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = formatYen(uiState.breakdown.finalPayable),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = if (uiState.breakdown.finalPayable < 0) MaterialTheme.colorScheme.error else CoralPrice,
              modifier = Modifier.testTag("checkout_final_payable_text"),
            )
          }
          Text(
            text = "共省 ${formatYen(totalSaved)}",
            style = MaterialTheme.typography.labelSmall,
            color = CoralPrice,
          )
        }

        Button(
          onClick = onSubmitOrder,
          shape = RoundedCornerShape(50),
          colors = ButtonDefaults.buttonColors(
            containerColor = BrandBlue,
            contentColor = Color.White,
          ),
          contentPadding = PaddingValues(horizontal = 20.dp, vertical = 11.dp),
          modifier = Modifier.testTag("checkout_submit_button"),
        ) {
          Text(
            text = "立即结算(${uiState.totalSelectedCount})",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
          )
        }
      }
    }
  }
}

@Composable
private fun OrderNoteCard(
  promoNote: String,
  onUpdatePromoNote: (String) -> Unit,
  onAppendQuickTag: (String) -> Unit,
) {
  val quickTags = listOf(
    "需要 GDG 上海限定手提袋",
    "附赠 Android Bot 徽章",
    "会场下午茶时段自提",
  )

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "订单备注与定制心愿",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
        )
        Text(
          text = "点击标签快捷填入",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        quickTags.forEach { tag ->
          Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(50),
            modifier = Modifier.clickable { onAppendQuickTag(tag) },
          ) {
            Text(
              text = "+ $tag",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            )
          }
        }
      }

      OutlinedTextField(
        value = promoNote,
        onValueChange = onUpdatePromoNote,
        label = { Text("订单备注（选填）") },
        placeholder = { Text("例如：需要 GDG DevFest Shanghai 纪念纸袋") },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("checkout_note_input"),
        singleLine = true,
      )
    }
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckoutScreenPreview() {
  DevFestCartLabTheme {
    CheckoutContent(
      uiState = CheckoutUiState(),
      onSelectCoupon = {},
      onUpdateQuantity = { _, _ -> },
      onUpdatePromoNote = {},
    )
  }
}
