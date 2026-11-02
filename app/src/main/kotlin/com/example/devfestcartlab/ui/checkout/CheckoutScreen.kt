package com.example.devfestcartlab.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.devfestcartlab.domain.CartItem
import com.example.devfestcartlab.ui.theme.DevFestCartLabTheme
import java.util.Locale

/**
 * 购物车结算页（Workshop Level B 靶子）
 *
 * 当前已知 UI 缺陷（请配合 `skills/edge-to-edge/SKILL.md` 修复）：
 * 1. `MainActivity` 已启用 `enableEdgeToEdge()`，但本页面未使用 `Scaffold` 传递 `innerPadding`，
 *    也未对顶部标题栏、滚动列表 (`LazyColumn`) 与底部「立即结算」栏应用 `WindowInsets` / `safeDrawingPadding()`。
 * 2. 底部结算栏 (`checkout_bottom_bar`) 直接贴合屏幕物理底边 (`Alignment.BottomCenter`)，在全面屏手势导航或三键导航下与系统导航栏重叠。
 * 3. 订单备注输入框 (`OutlinedTextField`) 未处理软键盘 `imePadding()` / `WindowInsetsRulers.Ime`。
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
    onUpdatePromoNote = viewModel::updatePromoNote,
    modifier = modifier,
  )
}

@Composable
internal fun CheckoutContent(
  uiState: CheckoutUiState,
  onSelectCoupon: (String) -> Unit,
  onUpdateQuantity: (String, Int) -> Unit,
  onUpdatePromoNote: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  // BUG (Workshop Level B): 未使用 Scaffold innerPadding 或 WindowInsets 安全区，导致顶栏、列表与底部结算按钮被系统栏遮挡
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("checkout_item_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        Text(
          text = "DevFest 购物车结算",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
          text = "全场满 ¥200 先享 8 折，再叠加优惠券（最低 ¥0.00 兜底）",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      items(uiState.items, key = { it.id }) { item ->
        CartItemCard(
          item = item,
          onDecrease = { onUpdateQuantity(item.id, -1) },
          onIncrease = { onUpdateQuantity(item.id, 1) },
        )
      }

      item {
        CouponSelectorSection(
          coupons = uiState.availableCoupons,
          selectedCouponId = uiState.selectedCouponId,
          onSelectCoupon = onSelectCoupon,
        )
      }

      item {
        OutlinedTextField(
          value = uiState.promoNote,
          onValueChange = onUpdatePromoNote,
          label = { Text("订单备注（选填）") },
          placeholder = { Text("例如：需要 DevFest 纪念袋") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("checkout_note_input"),
          singleLine = true,
        )
      }

      item {
        PriceBreakdownCard(uiState = uiState)
      }

      item {
        Spacer(modifier = Modifier.height(88.dp))
      }
    }

    // BUG (Workshop Level B): 底部固定结算栏未处理系统导航栏 Insets (navigationBarsPadding / Scaffold bottomBar)
    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .testTag("checkout_bottom_bar"),
      tonalElevation = 6.dp,
      shadowElevation = 8.dp,
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = "应付总额",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = formatCurrency(uiState.breakdown.finalPayable),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("checkout_final_payable_text"),
          )
        }
        Button(
          onClick = { /* 触发订单提交 */ },
          modifier = Modifier.testTag("checkout_submit_button"),
        ) {
          Text("立即结算")
        }
      }
    }
  }
}

@Composable
private fun CartItemCard(
  item: CartItem,
  onDecrease: () -> Unit,
  onIncrease: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
        )
        Text(
          text = "${formatCurrency(item.unitPrice)} × ${item.quantity}",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        OutlinedButton(onClick = onDecrease) { Text("-") }
        Text(
          text = item.quantity.toString(),
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Bold,
        )
        OutlinedButton(onClick = onIncrease) { Text("+") }
      }
    }
  }
}

@Composable
private fun CouponSelectorSection(
  coupons: List<CouponOption>,
  selectedCouponId: String,
  onSelectCoupon: (String) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(
      text = "选择优惠券",
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight.SemiBold,
    )
    coupons.forEach { coupon ->
      FilterChip(
        selected = coupon.id == selectedCouponId,
        onClick = { onSelectCoupon(coupon.id) },
        label = { Text(coupon.label) },
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

@Composable
private fun PriceBreakdownCard(uiState: CheckoutUiState) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = "价格明细",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
      )
      PriceRow("商品原价小计", formatCurrency(uiState.breakdown.subtotal))
      PriceRow("满 ¥200 打 8 折后小计", formatCurrency(uiState.breakdown.discountedSubtotal))
      PriceRow("优惠券抵扣", "-${formatCurrency(uiState.breakdown.couponApplied)}")
      HorizontalDivider()
      PriceRow(
        label = "最终应付金额",
        value = formatCurrency(uiState.breakdown.finalPayable),
        bold = true,
      )
    }
  }
}

@Composable
private fun PriceRow(label: String, value: String, bold: Boolean = false) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
    )
  }
}

private fun formatCurrency(amount: Double): String =
  String.format(Locale.US, "¥%.2f", amount)

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
