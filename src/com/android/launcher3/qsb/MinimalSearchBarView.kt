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
package com.android.launcher3.qsb

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import com.android.launcher3.R
import com.android.launcher3.graphics.AxBackdropBlurSurface
import com.android.launcher3.graphics.DockBackgroundStyle
import com.android.launcher3.views.ActivityContext

/**
 * Small capsule used by the "minimal" dock search bar.
 *
 * It renders its own rounded background (blurred when available, otherwise a translucent solid)
 * and opens the app drawer search when clicked.
 */
class MinimalSearchBarView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val blurSurface =
        AxBackdropBlurSurface(this, ActivityContext.lookupContextNoThrow(context))
    private val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pillBounds = RectF()
    private var blurTintColor = 0
    private var solidColor = 0
    private var blurred = true

    init {
        isClickable = true
        isFocusable = true
        contentDescription = context.getString(R.string.all_apps_search_bar_hint)
        setWillNotDraw(false)
    }

    /** Re-reads the dock appearance preferences and refreshes the capsule. */
    fun refreshStyle() {
        blurred = DockBackgroundStyle.isBlurred(context)
        blurTintColor = DockBackgroundStyle.blurTintColor(context)
        solidColor = DockBackgroundStyle.solidColor(context)
        updateRipple()
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshStyle()
    }

    override fun onDetachedFromWindow() {
        blurSurface.release()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        pillBounds.set(0f, 0f, w.toFloat(), h.toFloat())
        updateRipple()
    }

    override fun dispatchDraw(canvas: Canvas) {
        val radius = height / 2f
        var drewBlur = false
        if (blurred && blurSurface.isActive()) {
            drewBlur = blurSurface.drawRect(canvas, 0, 0, width, height, radius)
        }
        pillPaint.color = if (drewBlur) blurTintColor else solidColor
        canvas.drawRoundRect(pillBounds, radius, radius, pillPaint)
        super.dispatchDraw(canvas)
    }

    private fun updateRipple() {
        val mask =
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = height / 2f
                setColor(0xFFFFFFFF.toInt())
            }
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            android.R.attr.colorControlHighlight,
            typedValue,
            true,
        )
        foreground = RippleDrawable(ColorStateList.valueOf(typedValue.data), null, mask)
    }
}
