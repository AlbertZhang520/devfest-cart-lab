package com.example.devfestcartlab.ui.checkout

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devfestcartlab.domain.CartItem
import com.example.devfestcartlab.ui.theme.AndroidGreen
import com.example.devfestcartlab.ui.theme.AndroidGreenContainer
import com.example.devfestcartlab.ui.theme.BrandBlue
import com.example.devfestcartlab.ui.theme.CoralPrice
import com.example.devfestcartlab.ui.theme.PromoAmber
import java.util.Locale

@Composable
internal fun MallHeroBanner(
  subtotal: Double,
  threshold: Double,
  onResetCart: () -> Unit,
) {
  val progress = (subtotal / threshold).toFloat().coerceIn(0f, 1f)
  val isQualified = subtotal >= threshold
  val remaining = (threshold - subtotal).coerceAtLeast(0.0)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E3A8A),
            Color(0xFF1D4ED8),
          ),
        ),
      )
      .padding(horizontal = 16.dp, vertical = 14.dp),
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Surface(
            color = Color(0xFF34A853),
            shape = RoundedCornerShape(6.dp),
          ) {
            Text(
              text = "GDG SHANGHAI",
              color = Color.White,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            )
          }
          Column {
            Text(
              text = "DevFest 2026 官方周边商城",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
            )
            Text(
              text = "上海站限定周边 · 满 ¥200 先享 8 折，再叠神券",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFFBFDBFE),
            )
          }
        }

        Surface(
          color = Color.White.copy(alpha = 0.14f),
          shape = RoundedCornerShape(50),
          modifier = Modifier.clickable(onClick = onResetCart),
        ) {
          Text(
            text = "重置清单",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          )
        }
      }

      // Free Shipping / Full Discount Progress Pill
      Surface(
        color = Color.White.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 9.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Surface(
                color = if (isQualified) Color(0xFF22C55E) else PromoAmber,
                shape = RoundedCornerShape(4.dp),
              ) {
                Text(
                  text = if (isQualified) "已享 8 折" else "满折凑单",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                )
              }
              Text(
                text = if (isQualified) {
                  "已满 ¥200！全单原价直享 8 折优惠"
                } else {
                  "再买 ${formatYen(remaining)} 即可享全单 8 折"
                },
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium,
              )
            }
            Text(
              text = "${(progress * 100).toInt()}%",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF93C5FD),
            )
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(50))
              .background(Color.White.copy(alpha = 0.2f)),
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(
                  Brush.horizontalGradient(
                    listOf(Color(0xFF34D399), Color(0xFF60A5FA)),
                  ),
                ),
            )
          }
        }
      }
    }
  }
}

@Composable
internal fun DeliveryAddressCard(
  selectedMode: DeliveryMode,
  onSelectMode: (DeliveryMode) -> Unit,
) {
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
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.surfaceContainer)
          .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        DeliveryMode.entries.forEach { mode ->
          val selected = mode == selectedMode
          val bg by animateColorAsState(
            if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
            label = "deliveryTabBg",
          )
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(bg)
              .clickable { onSelectMode(mode) }
              .padding(vertical = 7.dp),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = if (mode == DeliveryMode.VENUE_PICKUP) "会场极速自提" else "顺丰同城/全国",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
              color = if (selected) BrandBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(BrandBlue.copy(alpha = 0.1f)),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = if (selectedMode == DeliveryMode.VENUE_PICKUP) "SH" else "SF",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            color = BrandBlue,
          )
        }
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Text(
              text = selectedMode.title,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Surface(
              color = AndroidGreenContainer,
              shape = RoundedCornerShape(4.dp),
            ) {
              Text(
                text = selectedMode.badge,
                style = MaterialTheme.typography.labelSmall,
                color = AndroidGreen,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
              )
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = selectedMode.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
internal fun MallMerchItemCard(
  item: CartItem,
  isSelected: Boolean,
  selectedSpecIndex: Int,
  onToggleSelect: () -> Unit,
  onCycleSpec: () -> Unit,
  onDecrease: () -> Unit,
  onIncrease: () -> Unit,
) {
  val meta = merchMetaFor(item.id)
  val specText = meta.specs.getOrElse(selectedSpecIndex % meta.specs.size) { meta.specs.first() }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      // Custom Mall Check Circle
      MallCheckCircle(
        checked = isSelected,
        onClick = onToggleSelect,
      )

      // Rich Merch Thumbnail Illustration
      MerchThumbnailArtwork(
        kind = meta.visualKind,
        badge = meta.badge,
      )

      // Product Details + Price + Stepper
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(5.dp),
      ) {
        Text(
          text = item.name,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        Text(
          text = meta.subtitle,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        // Interactive SKU Spec Pill
        Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier.clickable(onClick = onCycleSpec),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              text = specText,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Medium,
            )
            Text(
              text = "▾",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
        ) {
          Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
          ) {
            Text(
              text = formatYen(item.unitPrice),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = CoralPrice,
            )
            Text(
              text = formatYen(meta.originalPrice),
              style = MaterialTheme.typography.labelSmall.copy(
                textDecoration = TextDecoration.LineThrough,
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
          }

          // Compact Mall Quantity Stepper
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onDecrease),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = "−",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
            Box(
              modifier = Modifier
                .width(30.dp)
                .height(28.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = item.quantity.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
            Box(
              modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onIncrease),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = "+",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = BrandBlue,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
internal fun AddonMerchSection(
  candidates: List<MerchItemUiMeta>,
  onAddItem: (String) -> Unit,
) {
  if (candidates.isEmpty()) return

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "上海站人气加购 · 极速凑满 ¥200",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = "点击一键加购",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      candidates.forEach { meta ->
        Card(
          modifier = Modifier.width(210.dp),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            MerchThumbnailArtwork(
              kind = meta.visualKind,
              badge = meta.badge,
              sizeDp = 52,
            )
            Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Text(
                text = meta.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = formatYen(meta.unitPrice),
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.ExtraBold,
                  color = CoralPrice,
                )
                Surface(
                  color = BrandBlue,
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.clickable { onAddItem(meta.id) },
                ) {
                  Text(
                    text = "+加购",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
internal fun CouponTicketSection(
  coupons: List<CouponOption>,
  selectedCouponId: String,
  onSelectCoupon: (String) -> Unit,
) {
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
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Surface(
            color = CoralPrice,
            shape = RoundedCornerShape(4.dp),
          ) {
            Text(
              text = "神券",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
            )
          }
          Text(
            text = "DevFest 专属优惠券",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
          )
        }
        Text(
          text = "满折后可叠加使用",
          style = MaterialTheme.typography.labelSmall,
          color = CoralPrice,
          fontWeight = FontWeight.SemiBold,
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        coupons.forEach { coupon ->
          val isSelected = coupon.id == selectedCouponId
          val borderColor = if (isSelected) CoralPrice else MaterialTheme.colorScheme.outlineVariant
          val containerBg =
            if (isSelected) Color(0xFFFFF5F5) else MaterialTheme.colorScheme.surfaceContainerLow

          Surface(
            modifier = Modifier
              .width(164.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable { onSelectCoupon(coupon.id) },
            color = containerBg,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
          ) {
            Column(
              modifier = Modifier.padding(10.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = if (coupon.amount > 0) "-¥${coupon.amount.toInt()}" else "¥0",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (coupon.amount > 0) CoralPrice else MaterialTheme.colorScheme.onSurface,
                )
                Surface(
                  color = if (isSelected) CoralPrice else MaterialTheme.colorScheme.surfaceContainer,
                  shape = RoundedCornerShape(4.dp),
                ) {
                  Text(
                    text = if (isSelected) "已选" else coupon.tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                  )
                }
              }
              Text(
                text = coupon.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
              Text(
                text = coupon.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
internal fun PriceBreakdownReceiptCard(uiState: CheckoutUiState) {
  val breakdown = uiState.breakdown
  val hasNegativeBug = breakdown.finalPayable < 0.0
  val totalSaved =
    (breakdown.subtotal - breakdown.finalPayable).coerceAtLeast(0.0)

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "结算金额明细",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
        )
        Surface(
          color = BrandBlue.copy(alpha = 0.1f),
          shape = RoundedCornerShape(6.dp),
        ) {
          Text(
            text = "已选 ${uiState.totalSelectedCount} 件商品",
            style = MaterialTheme.typography.labelSmall,
            color = BrandBlue,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          )
        }
      }

      ReceiptLine(
        label = "商品原价小计",
        value = formatYen(breakdown.subtotal),
      )
      ReceiptLine(
        label = "DevFest 满 ¥200 享 8 折后小计",
        value = formatYen(breakdown.discountedSubtotal),
        badge = "核心算价项",
      )
      ReceiptLine(
        label = "优惠券抵扣",
        value = "-${formatYen(breakdown.couponApplied)}",
        valueColor = CoralPrice,
      )
      ReceiptLine(
        label = "会场配送 / 运费",
        value = "¥0.00",
        badge = "免运费",
      )

      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = "最终应付总额",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = "累计已省 ${formatYen(totalSaved)}",
            style = MaterialTheme.typography.labelSmall,
            color = CoralPrice,
          )
        }
        Text(
          text = formatYen(breakdown.finalPayable),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = if (hasNegativeBug) MaterialTheme.colorScheme.error else CoralPrice,
        )
      }

      if (hasNegativeBug) {
        Surface(
          color = MaterialTheme.colorScheme.errorContainer,
          shape = RoundedCornerShape(10.dp),
        ) {
          Text(
            text = "⚠️ 检测到负数应付金额 (${formatYen(breakdown.finalPayable)})！请通过 Level A 修复 CartPriceCalculator 0 元兜底逻辑。",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(10.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun ReceiptLine(
  label: String,
  value: String,
  badge: String? = null,
  valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      if (badge != null) {
        Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = RoundedCornerShape(4.dp),
        ) {
          Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
          )
        }
      }
    }
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.SemiBold,
      color = valueColor,
    )
  }
}

@Composable
internal fun MallCheckCircle(
  checked: Boolean,
  onClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .size(22.dp)
      .clip(CircleShape)
      .background(if (checked) BrandBlue else Color.Transparent)
      .border(
        width = 1.5.dp,
        color = if (checked) BrandBlue else MaterialTheme.colorScheme.outlineVariant,
        shape = CircleShape,
      )
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    if (checked) {
      Text(
        text = "✓",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
      )
    }
  }
}

@Composable
internal fun MerchThumbnailArtwork(
  kind: MerchVisualKind,
  badge: String,
  sizeDp: Int = 78,
) {
  val gradient =
    when (kind) {
      MerchVisualKind.HOODIE -> listOf(Color(0xFF1E293B), Color(0xFF334155))
      MerchVisualKind.MUG -> listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
      MerchVisualKind.STICKER -> listOf(Color(0xFF059669), Color(0xFF34D399))
      MerchVisualKind.KEYCAP -> listOf(Color(0xFF7C3AED), Color(0xFFA78BFA))
      MerchVisualKind.BOT_FIGURE -> listOf(Color(0xFF16A34A), Color(0xFF4ADE80))
    }

  Box(
    modifier = Modifier
      .size(sizeDp.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(Brush.linearGradient(gradient)),
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      drawCircle(
        color = Color.White.copy(alpha = 0.10f),
        radius = w * 0.44f,
        center = Offset(w * 0.78f, h * 0.25f),
      )

      when (kind) {
        MerchVisualKind.HOODIE -> {
          // Hoodie silhouette + terminal prompt icon
          drawRoundRect(
            color = Color.White.copy(alpha = 0.9f),
            topLeft = Offset(w * 0.24f, h * 0.26f),
            size = Size(w * 0.52f, h * 0.52f),
            cornerRadius = CornerRadius(12f, 12f),
          )
          drawCircle(
            color = Color(0xFF1E293B),
            radius = w * 0.11f,
            center = Offset(w * 0.5f, h * 0.26f),
          )
          drawRoundRect(
            color = Color(0xFF38BDF8),
            topLeft = Offset(w * 0.34f, h * 0.42f),
            size = Size(w * 0.32f, h * 0.18f),
            cornerRadius = CornerRadius(6f, 6f),
          )
        }
        MerchVisualKind.MUG -> {
          // Coffee Mug + handle + steam
          drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.24f, h * 0.32f),
            size = Size(w * 0.44f, h * 0.44f),
            cornerRadius = CornerRadius(10f, 10f),
          )
          drawArc(
            color = Color.White,
            startAngle = -70f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(w * 0.56f, h * 0.38f),
            size = Size(w * 0.22f, h * 0.30f),
            style = Stroke(width = 6f, cap = StrokeCap.Round),
          )
        }
        MerchVisualKind.STICKER -> {
          // Android Bot head + antennas
          drawArc(
            color = Color.White,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(w * 0.24f, h * 0.34f),
            size = Size(w * 0.52f, h * 0.48f),
          )
          drawCircle(
            color = Color(0xFF059669),
            radius = w * 0.04f,
            center = Offset(w * 0.40f, h * 0.48f),
          )
          drawCircle(
            color = Color(0xFF059669),
            radius = w * 0.04f,
            center = Offset(w * 0.60f, h * 0.48f),
          )
        }
        MerchVisualKind.KEYCAP -> {
          drawRoundRect(
            color = Color.White.copy(alpha = 0.92f),
            topLeft = Offset(w * 0.22f, h * 0.22f),
            size = Size(w * 0.56f, h * 0.56f),
            cornerRadius = CornerRadius(14f, 14f),
          )
          val path = Path().apply {
            moveTo(w * 0.34f, h * 0.34f)
            lineTo(w * 0.66f, h * 0.34f)
            lineTo(w * 0.50f, h * 0.50f)
            lineTo(w * 0.66f, h * 0.66f)
            lineTo(w * 0.34f, h * 0.66f)
            close()
          }
          drawPath(path, color = Color(0xFF7C3AED))
        }
        MerchVisualKind.BOT_FIGURE -> {
          drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.28f, h * 0.28f),
            size = Size(w * 0.44f, h * 0.46f),
            cornerRadius = CornerRadius(16f, 16f),
          )
          drawCircle(
            color = Color(0xFF16A34A),
            radius = w * 0.045f,
            center = Offset(w * 0.42f, h * 0.44f),
          )
          drawCircle(
            color = Color(0xFF16A34A),
            radius = w * 0.045f,
            center = Offset(w * 0.58f, h * 0.44f),
          )
        }
      }
    }

    if (sizeDp >= 70) {
      Surface(
        color = Color.Black.copy(alpha = 0.55f),
        shape = RoundedCornerShape(topStart = 14.dp, bottomEnd = 8.dp),
        modifier = Modifier.align(Alignment.TopStart),
      ) {
        Text(
          text = badge,
          color = Color.White,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
        )
      }
    }
  }
}

@Composable
internal fun OrderSubmittedSummaryDialog(
  uiState: CheckoutUiState,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Surface(
          color = AndroidGreenContainer,
          shape = RoundedCornerShape(8.dp),
        ) {
          Text(
            text = "上海站自提码",
            style = MaterialTheme.typography.labelSmall,
            color = AndroidGreen,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
          )
        }
        Text(
          text = "订单核销预览",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold,
        )
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "取货方式：${uiState.deliveryMode.title}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = RoundedCornerShape(10.dp),
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              text = "核销提货码：SH-2026-8848",
              fontFamily = FontFamily.Monospace,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.ExtraBold,
              color = BrandBlue,
            )
            Text(
              text = "实付金额：${formatYen(uiState.breakdown.finalPayable)}（含已选 ${uiState.totalSelectedCount} 件周边）",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.SemiBold,
            )
            if (uiState.promoNote.isNotBlank()) {
              Text(
                text = "备注：${uiState.promoNote}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
      ) {
        Text("好的，我知道了")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("继续逛逛")
      }
    },
  )
}

internal fun formatYen(amount: Double): String =
  String.format(Locale.US, "¥%.2f", amount)
