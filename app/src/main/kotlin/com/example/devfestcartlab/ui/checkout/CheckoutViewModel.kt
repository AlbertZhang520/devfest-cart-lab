package com.example.devfestcartlab.ui.checkout

import androidx.lifecycle.ViewModel
import com.example.devfestcartlab.domain.CartItem
import com.example.devfestcartlab.domain.CartPriceCalculator
import com.example.devfestcartlab.domain.DiscountRule
import com.example.devfestcartlab.domain.PriceBreakdown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class MerchVisualKind {
  HOODIE,
  MUG,
  STICKER,
  KEYCAP,
  BOT_FIGURE,
}

enum class DeliveryMode(val title: String, val subtitle: String, val badge: String) {
  VENUE_PICKUP(
    title = "GDG DevFest Shanghai 会场自提点",
    subtitle = "上海主会场 B1 开发者市集 · 凭核销码立取",
    badge = "免运费 · 极速取货",
  ),
  EXPRESS_SHIPPING(
    title = "顺丰特快包邮到家",
    subtitle = "上海市浦东新区张江高科园区 · 预计明天送达",
    badge = "会员免邮",
  ),
}

data class MerchItemUiMeta(
  val id: String,
  val name: String,
  val subtitle: String,
  val unitPrice: Double,
  val originalPrice: Double,
  val badge: String,
  val specs: List<String>,
  val visualKind: MerchVisualKind,
)

data class CouponOption(
  val id: String,
  val label: String,
  val subtitle: String,
  val tag: String,
  val amount: Double,
)

data class CheckoutUiState(
  val items: List<CartItem> = defaultCartItems(),
  val selectedItemIds: Set<String> = defaultCartItems().map { it.id }.toSet(),
  val selectedSpecs: Map<String, Int> = emptyMap(),
  val deliveryMode: DeliveryMode = DeliveryMode.VENUE_PICKUP,
  val availableCoupons: List<CouponOption> = defaultCoupons(),
  val selectedCouponId: String = "coupon_20",
  val promoNote: String = "",
  val showOrderSubmittedDialog: Boolean = false,
  val breakdown: PriceBreakdown =
    CartPriceCalculator().calculate(
      items = defaultCartItems(),
      rule = DiscountRule(threshold = 200.0, discountRate = 0.8, couponAmount = 20.0),
    ),
) {
  val selectedItems: List<CartItem>
    get() = items.filter { it.id in selectedItemIds }

  val totalSelectedCount: Int
    get() = selectedItems.sumOf { it.quantity }

  val isAllSelected: Boolean
    get() = items.isNotEmpty() && selectedItemIds.size == items.size

  val addonCandidates: List<MerchItemUiMeta>
    get() {
      val existingIds = items.map { it.id }.toSet()
      return allMerchCatalog.values.filter { it.id !in existingIds }
    }
}

class CheckoutViewModel(
  private val calculator: CartPriceCalculator = CartPriceCalculator(),
) : ViewModel() {

  private val _uiState = MutableStateFlow(CheckoutUiState())
  val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

  fun selectCoupon(couponId: String) {
    _uiState.update { current ->
      recalculate(current.copy(selectedCouponId = couponId))
    }
  }

  fun updateQuantity(itemId: String, delta: Int) {
    _uiState.update { current ->
      val target = current.items.firstOrNull { it.id == itemId } ?: return@update current
      val nextQty = target.quantity + delta
      val updatedItems =
        if (nextQty <= 0 && current.items.size > 1) {
          current.items.filterNot { it.id == itemId }
        } else {
          current.items.map { item ->
            if (item.id == itemId) item.copy(quantity = nextQty.coerceAtLeast(1)) else item
          }
        }
      val updatedSelected =
        current.selectedItemIds.intersect(updatedItems.map { it.id }.toSet())
      recalculate(
        current.copy(
          items = updatedItems,
          selectedItemIds = updatedSelected,
        ),
      )
    }
  }

  fun toggleItemSelection(itemId: String) {
    _uiState.update { current ->
      val nextSelected =
        if (itemId in current.selectedItemIds) {
          if (current.selectedItemIds.size > 1) current.selectedItemIds - itemId else current.selectedItemIds
        } else {
          current.selectedItemIds + itemId
        }
      recalculate(current.copy(selectedItemIds = nextSelected))
    }
  }

  fun toggleSelectAll() {
    _uiState.update { current ->
      val allIds = current.items.map { it.id }.toSet()
      val nextSelected =
        if (current.selectedItemIds.size == allIds.size) {
          setOfNotNull(current.items.firstOrNull()?.id)
        } else {
          allIds
        }
      recalculate(current.copy(selectedItemIds = nextSelected))
    }
  }

  fun cycleItemSpec(itemId: String) {
    _uiState.update { current ->
      val meta = merchMetaFor(itemId)
      val currentIndex = current.selectedSpecs[itemId] ?: 0
      val nextIndex = (currentIndex + 1) % meta.specs.size
      current.copy(selectedSpecs = current.selectedSpecs + (itemId to nextIndex))
    }
  }

  fun addCatalogItem(itemId: String) {
    _uiState.update { current ->
      val meta = allMerchCatalog[itemId] ?: return@update current
      if (current.items.any { it.id == itemId }) return@update current
      val newItem =
        CartItem(
          id = meta.id,
          name = meta.name,
          unitPrice = meta.unitPrice,
          quantity = 1,
        )
      val updatedItems = current.items + newItem
      val updatedSelected = current.selectedItemIds + meta.id
      recalculate(
        current.copy(
          items = updatedItems,
          selectedItemIds = updatedSelected,
        ),
      )
    }
  }

  fun resetCart() {
    _uiState.value = CheckoutUiState()
  }

  fun selectDeliveryMode(mode: DeliveryMode) {
    _uiState.update { it.copy(deliveryMode = mode) }
  }

  fun updatePromoNote(note: String) {
    _uiState.update { it.copy(promoNote = note) }
  }

  fun appendQuickNoteTag(tag: String) {
    _uiState.update { current ->
      val nextNote =
        when {
          current.promoNote.isBlank() -> tag
          current.promoNote.contains(tag) -> current.promoNote
          else -> "${current.promoNote}；$tag"
        }
      current.copy(promoNote = nextNote)
    }
  }

  fun submitOrder() {
    _uiState.update { it.copy(showOrderSubmittedDialog = true) }
  }

  fun dismissOrderDialog() {
    _uiState.update { it.copy(showOrderSubmittedDialog = false) }
  }

  private fun recalculate(state: CheckoutUiState): CheckoutUiState {
    val couponAmount =
      state.availableCoupons.firstOrNull { it.id == state.selectedCouponId }?.amount ?: 0.0
    val breakdown =
      calculator.calculate(
        items = state.selectedItems,
        rule = DiscountRule(threshold = 200.0, discountRate = 0.8, couponAmount = couponAmount),
      )
    return state.copy(breakdown = breakdown)
  }
}

private val allMerchCatalog: Map<String, MerchItemUiMeta> =
  listOf(
    MerchItemUiMeta(
      id = "hoodie",
      name = "Android CLI 极客重磅连帽卫衣",
      subtitle = "420g 纯棉毛圈 · 终端命令行夜光印花",
      unitPrice = 200.0,
      originalPrice = 269.0,
      badge = "会场爆款",
      specs = listOf("极客黑 · L码", "极客黑 · XL码", "冰川灰 · M码"),
      visualKind = MerchVisualKind.HOODIE,
    ),
    MerchItemUiMeta(
      id = "mug",
      name = "Jetpack Compose 温感变色马克杯",
      subtitle = "注入热水浮现 Recomposition 状态树",
      unitPrice = 60.0,
      originalPrice = 79.0,
      badge = "满减百搭",
      specs = listOf("骨瓷白 · 350ml", "深空黑 · 420ml"),
      visualKind = MerchVisualKind.MUG,
    ),
    MerchItemUiMeta(
      id = "sticker",
      name = "DevFest 2026 全息镭射贴纸包",
      subtitle = "含 Android Bot / Gemini / Kotlin 12 枚装",
      unitPrice = 35.0,
      originalPrice = 49.0,
      badge = "限量周边",
      specs = listOf("全息镭射 · 12枚/袋", "哑光防水 · 12枚/袋"),
      visualKind = MerchVisualKind.STICKER,
    ),
    MerchItemUiMeta(
      id = "keycap",
      name = "Kotlin 机械键盘手工透光键帽",
      subtitle = "PBT 热升华 + CNC 阳极氧化铝 ESC 键",
      unitPrice = 85.0,
      originalPrice = 109.0,
      badge = "凑单神器",
      specs = listOf("原厂高度 · 紫金配色", "OEM高度 · 终端绿"),
      visualKind = MerchVisualKind.KEYCAP,
    ),
    MerchItemUiMeta(
      id = "bot_figure",
      name = "Android Bot 2026 磁吸联名手办",
      subtitle = "可动关节 + 磁吸迷你笔记本配件",
      unitPrice = 150.0,
      originalPrice = 199.0,
      badge = "典藏编号",
      specs = listOf("经典安卓绿 · 礼盒装", "电镀银限定 · 礼盒装"),
      visualKind = MerchVisualKind.BOT_FIGURE,
    ),
  ).associateBy { it.id }

internal fun merchMetaFor(itemId: String): MerchItemUiMeta =
  allMerchCatalog[itemId]
    ?: MerchItemUiMeta(
      id = itemId,
      name = itemId,
      subtitle = "GDG DevFest 官方正版纪念周边",
      unitPrice = 100.0,
      originalPrice = 129.0,
      badge = "官方自营",
      specs = listOf("标准规格"),
      visualKind = MerchVisualKind.HOODIE,
    )

private fun defaultCartItems(): List<CartItem> =
  listOf(
    CartItem(
      id = "hoodie",
      name = "Android CLI 极客重磅连帽卫衣",
      unitPrice = 200.0,
      quantity = 1,
    ),
    CartItem(
      id = "mug",
      name = "Jetpack Compose 温感变色马克杯",
      unitPrice = 60.0,
      quantity = 1,
    ),
    CartItem(
      id = "sticker",
      name = "DevFest 2026 全息镭射贴纸包",
      unitPrice = 35.0,
      quantity = 1,
    ),
  )

private fun defaultCoupons(): List<CouponOption> =
  listOf(
    CouponOption(
      id = "coupon_20",
      label = "满减叠加神券",
      subtitle = "折后可再抵 ¥20 · 全场通用",
      tag = "推荐叠加",
      amount = 20.0,
    ),
    CouponOption(
      id = "coupon_300",
      label = "DevFest 讲师免单券",
      subtitle = "无门槛立减 ¥300（可测 0 元兜底）",
      tag = "Bug 探测器",
      amount = 300.0,
    ),
    CouponOption(
      id = "none",
      label = "暂不使用优惠券",
      subtitle = "仅享受满 ¥200 打 8 折活动",
      tag = "仅享满折",
      amount = 0.0,
    ),
  )
