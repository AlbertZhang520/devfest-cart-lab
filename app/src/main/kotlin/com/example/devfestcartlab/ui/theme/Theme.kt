/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.devfestcartlab.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF68A7FF),
    onPrimary = Color(0xFF002C66),
    primaryContainer = Color(0xFF1E3A5F),
    onPrimaryContainer = Color(0xFFD6E6FF),
    secondary = CoralPriceDark,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF4C1D1B),
    onSecondaryContainer = Color(0xFFFFDAD6),
    tertiary = PromoAmber,
    background = MallBackgroundDark,
    onBackground = MallTextPrimaryDark,
    surface = MallSurfaceDark,
    onSurface = MallTextPrimaryDark,
    surfaceVariant = MallSurfaceContainerDark,
    onSurfaceVariant = MallTextSecondaryDark,
    surfaceContainerLowest = MallSurfaceDark,
    surfaceContainerLow = MallSurfaceLowDark,
    surfaceContainer = MallSurfaceContainerDark,
    outline = MallOutlineDark,
    outlineVariant = MallOutlineDark,
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = BrandBlueContainer,
    onPrimaryContainer = OnBrandBlueContainer,
    secondary = CoralPrice,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = CoralContainer,
    onSecondaryContainer = OnCoralContainer,
    tertiary = PromoAmber,
    background = MallBackgroundLight,
    onBackground = MallTextPrimaryLight,
    surface = MallSurfaceLight,
    onSurface = MallTextPrimaryLight,
    surfaceVariant = MallSurfaceContainerLight,
    onSurfaceVariant = MallTextSecondaryLight,
    surfaceContainerLowest = MallSurfaceLight,
    surfaceContainerLow = MallSurfaceLowLight,
    surfaceContainer = MallSurfaceContainerLight,
    outline = MallOutlineLight,
    outlineVariant = MallOutlineLight,
)

@Composable
fun DevFestCartLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

