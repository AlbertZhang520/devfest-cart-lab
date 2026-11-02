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

data class CouponOption(
  val id: String,
  val label: String,
  val amount: Double,
)

data class CheckoutUiState(
  val items: List<CartItem> = defaultCartItems(),
  val availableCoupons: List<CouponOption> = defaultCoupons(),
  val selectedCouponId: String = "coupon_20",
  val promoNote: String = "",
  val breakdown: PriceBreakdown =
    CartPriceCalculator().calculate(
      items = defaultCartItems(),
      rule = DiscountRule(threshold = 200.0, discountRate = 0.8, couponAmount = 20.0),
    ),
)

class CheckoutViewModel(
  private val calculator: CartPriceCalculator = CartPriceCalculator(),
) : ViewModel() {

  private val _uiState = MutableStateFlow(CheckoutUiState())
  val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

  fun selectCoupon(couponId: String) {
    _uiState.update { current ->
      val couponAmount =
        current.availableCoupons.firstOrNull { it.id == couponId }?.amount ?: 0.0
      val breakdown =
        calculator.calculate(
          items = current.items,
          rule = DiscountRule(threshold = 200.0, discountRate = 0.8, couponAmount = couponAmount),
        )
      current.copy(
        selectedCouponId = couponId,
        breakdown = breakdown,
      )
    }
  }

  fun updateQuantity(itemId: String, delta: Int) {
    _uiState.update { current ->
      val updatedItems =
        current.items.map { item ->
          if (item.id == itemId) {
            item.copy(quantity = (item.quantity + delta).coerceAtLeast(1))
          } else {
            item
          }
        }
      val couponAmount =
        current.availableCoupons.firstOrNull { it.id == current.selectedCouponId }?.amount ?: 0.0
      val breakdown =
        calculator.calculate(
          items = updatedItems,
          rule = DiscountRule(threshold = 200.0, discountRate = 0.8, couponAmount = couponAmount),
        )
      current.copy(
        items = updatedItems,
        breakdown = breakdown,
      )
    }
  }

  fun updatePromoNote(note: String) {
    _uiState.update { it.copy(promoNote = note) }
  }
}

private fun defaultCartItems(): List<CartItem> =
  listOf(
    CartItem(
      id = "hoodie",
      name = "Android CLI 极客卫衣",
      unitPrice = 200.0,
      quantity = 1,
    ),
    CartItem(
      id = "mug",
      name = "Jetpack Compose 马克杯",
      unitPrice = 60.0,
      quantity = 1,
    ),
    CartItem(
      id = "sticker",
      name = "DevFest 2026 限量贴纸包",
      unitPrice = 35.0,
      quantity = 1,
    ),
  )

private fun defaultCoupons(): List<CouponOption> =
  listOf(
    CouponOption(id = "none", label = "不使用优惠券", amount = 0.0),
    CouponOption(id = "coupon_20", label = "满减叠加券 -¥20", amount = 20.0),
    CouponOption(id = "coupon_300", label = "DevFest 免单神券 -¥300", amount = 300.0),
  )
