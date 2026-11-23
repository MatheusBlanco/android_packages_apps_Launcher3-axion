/*
 * Copyright (C) 2025-2026 AxionOS
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

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.android.launcher3.LauncherPrefsExt
import com.android.launcher3.dagger.LauncherComponentProvider.appComponent
import com.android.launcher3.util.SandboxApplication
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class DockBackgroundStyleTest {

    @get:Rule val context = SandboxApplication()

    @Test
    fun defaults() {
        assertFalse(DockBackgroundStyle.isEnabled(context))
        assertTrue(DockBackgroundStyle.isBlurred(context))
        assertFalse(DockBackgroundStyle.isCustomColor(context))
        assertEquals(
            DockBackgroundStyle.DEFAULT_OPACITY,
            DockBackgroundStyle.opacityPercent(context),
        )
        assertEquals(
            DockBackgroundStyle.DEFAULT_RADIUS_DP,
            DockBackgroundStyle.cornerRadiusDp(context),
        )
    }

    @Test
    fun opacity_and_radius_are_clamped() {
        val prefs = context.appComponent.launcherPrefs
        prefs.put(LauncherPrefsExt.DOCK_BG_OPACITY, 500)
        prefs.put(LauncherPrefsExt.DOCK_BG_RADIUS, 500)
        assertEquals(DockBackgroundStyle.MAX_OPACITY, DockBackgroundStyle.opacityPercent(context))
        assertEquals(DockBackgroundStyle.MAX_RADIUS_DP, DockBackgroundStyle.cornerRadiusDp(context))

        prefs.put(LauncherPrefsExt.DOCK_BG_OPACITY, -10)
        prefs.put(LauncherPrefsExt.DOCK_BG_RADIUS, -10)
        assertEquals(DockBackgroundStyle.MIN_OPACITY, DockBackgroundStyle.opacityPercent(context))
        assertEquals(DockBackgroundStyle.MIN_RADIUS_DP, DockBackgroundStyle.cornerRadiusDp(context))
    }

    @Test
    fun custom_color_is_used_for_solid_fill() {
        val prefs = context.appComponent.launcherPrefs
        val custom = 0xFF336699.toInt()
        prefs.put(LauncherPrefsExt.DOCK_BG_COLOR_SOURCE, DockBackgroundStyle.COLOR_SOURCE_CUSTOM)
        prefs.put(LauncherPrefsExt.DOCK_BG_COLOR, custom)
        prefs.put(LauncherPrefsExt.DOCK_BG_OPACITY, 100)

        assertTrue(DockBackgroundStyle.isCustomColor(context))
        assertEquals(custom, DockBackgroundStyle.customColorOrFallback(context))
        assertEquals(custom, DockBackgroundStyle.solidColor(context))

        prefs.put(LauncherPrefsExt.DOCK_BG_OPACITY, 50)
        assertEquals(0x80, Color.alpha(DockBackgroundStyle.solidColor(context)))
    }

    @Test
    fun missing_custom_color_falls_back_to_white() {
        assertEquals(Color.WHITE, DockBackgroundStyle.customColorOrFallback(context))
    }
}
