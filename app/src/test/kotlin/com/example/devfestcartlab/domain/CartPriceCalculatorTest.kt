package com.example.devfestcartlab.domain

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 购物车价格计算验收单测（Read-Only 验收靶子，禁止修改）
 */
class CartPriceCalculatorTest {

  private val calculator = CartPriceCalculator()

  @Test
  fun calculateFinalPrice_belowThresholdWithoutCoupon_returnsSubtotal() {
    val items =
      listOf(
        CartItem(id = "mug", name = "Jetpack Compose Mug", unitPrice = 60.0, quantity = 2),
      )
    val rule = DiscountRule(threshold = 200.0, discountRate = 0.8, couponAmount = 0.0)

    val payable = calculator.calculateFinalPrice(items, rule)

    assertEquals(120.0, payable, 0.001)
  }

  @Test
  fun calculateFinalPrice_appliesThresholdDiscountBeforeCoupon() {
    // 原价恰好满 200 元：应先享 8 折（200 * 0.8 = 160），再减 20 元券 = 140.0 元
    val items =
      listOf(
        CartItem(id = "hoodie", name = "Android CLI Hoodie", unitPrice = 200.0, quantity = 1),
      )
    val rule = DiscountRule(threshold = 200.0, discountRate = 0.8, couponAmount = 20.0)

    val breakdown = calculator.calculate(items, rule)

    assertEquals(200.0, breakdown.subtotal, 0.001)
    assertEquals(160.0, breakdown.discountedSubtotal, 0.001)
    assertEquals(140.0, breakdown.finalPayable, 0.001)
  }

  @Test
  fun calculateFinalPrice_thresholdCheckedAgainstOriginalSubtotal() {
    // 原价 250 元：先享 8 折（250 * 0.8 = 200），再减 30 元券 = 170.0 元
    val items =
      listOf(
        CartItem(id = "hoodie", name = "Android CLI Hoodie", unitPrice = 150.0, quantity = 1),
        CartItem(id = "cap", name = "Kotlin Conf Cap", unitPrice = 50.0, quantity = 2),
      )
    val rule = DiscountRule(threshold = 200.0, discountRate = 0.8, couponAmount = 30.0)

    val payable = calculator.calculateFinalPrice(items, rule)

    assertEquals(170.0, payable, 0.001)
  }

  @Test
  fun calculateFinalPrice_whenCouponExceedsDiscountedTotal_clampsToZero() {
    // 原价 35 元，使用 50 元大额无门槛券：应付金额必须兜底为 0.0 元，不能出现 -15.0 元
    val items =
      listOf(
        CartItem(id = "sticker", name = "Android Bot Sticker Pack", unitPrice = 35.0, quantity = 1),
      )
    val rule = DiscountRule(threshold = 200.0, discountRate = 0.8, couponAmount = 50.0)

    val payable = calculator.calculateFinalPrice(items, rule)

    assertEquals(0.0, payable, 0.001)
  }
}
