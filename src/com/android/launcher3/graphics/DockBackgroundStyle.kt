/*
 * Copyright 2025-2026 AxionOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3.graphics

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.android.axion.blur.AxBlurColors
import com.android.launcher3.LauncherPrefsExt
import com.android.launcher3.Utilities
import kotlin.math.roundToInt

/**
 * Resolves the user-configured appearance of the dock background and the minimal search pill.
 */
object DockBackgroundStyle {
    const val STYLE_BLUR = "blur"
    const val STYLE_SOLID = "solid"

    const val COLOR_SOURCE_MONET = "monet"
    const val COLOR_SOURCE_CUSTOM = "custom"

    const val DEFAULT_OPACITY = 60
    const val DEFAULT_RADIUS_DP = 40

    const val MIN_OPACITY = 0
    const val MAX_OPACITY = 100
    const val MIN_RADIUS_DP = 0
    const val MAX_RADIUS_DP = 48

    @JvmStatic
    fun isEnabled(context: Context): Boolean = LauncherPrefsExt.DOCK_BG_ENABLED.get(context)

    @JvmStatic
    fun isBlurred(context: Context): Boolean =
        LauncherPrefsExt.DOCK_BG_STYLE.get(context) != STYLE_SOLID

    @JvmStatic
    fun isCustomColor(context: Context): Boolean =
        LauncherPrefsExt.DOCK_BG_COLOR_SOURCE.get(context) == COLOR_SOURCE_CUSTOM

    @JvmStatic
    fun opacityPercent(context: Context): Int =
        Utilities.boundToRange(
            LauncherPrefsExt.DOCK_BG_OPACITY.get(context),
            MIN_OPACITY,
            MAX_OPACITY,
        )

    @JvmStatic
    fun cornerRadiusPx(context: Context): Float =
        Utilities.dpToPx(cornerRadiusDp(context).toFloat(), context).toFloat()

    @JvmStatic
    fun cornerRadiusDp(context: Context): Int =
        Utilities.boundToRange(
            LauncherPrefsExt.DOCK_BG_RADIUS.get(context),
            MIN_RADIUS_DP,
            MAX_RADIUS_DP,
        )

    /** Color used for the tint drawn on top of the blurred dock surface. */
    @JvmStatic
    fun blurTintColor(context: Context): Int =
        applyOpacity(baseColor(context, blurred = true), opacityPercent(context))

    /** Color used when the dock surface is drawn solid (or when blur is unavailable). */
    @JvmStatic
    fun solidColor(context: Context): Int =
        applyOpacity(baseColor(context, blurred = false), opacityPercent(context))

    /** The opaque custom color, or a sensible fallback for the color picker. */
    @JvmStatic
    fun customColorOrFallback(context: Context): Int {
        val stored = LauncherPrefsExt.DOCK_BG_COLOR.get(context)
        return if (Color.alpha(stored) == 0) Color.WHITE else stored
    }

    private fun baseColor(context: Context, blurred: Boolean): Int {
        if (isCustomColor(context)) {
            return customColorOrFallback(context)
        }
        return if (blurred) {
            AxBlurColors.surfaceEffect0(context)
        } else {
            AxBlurColors.fallback(context)
        }
    }

    private fun applyOpacity(color: Int, opacityPercent: Int): Int {
        val alpha = (Color.alpha(color) * opacityPercent / 100f).roundToInt()
        return ColorUtils.setAlphaComponent(color, Utilities.boundToRange(alpha, 0, 255))
    }
}
