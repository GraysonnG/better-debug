package com.blanktheevil.betterdebug.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.blanktheevil.betterdebug.patches.SimpleProfilerPatches
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.helpers.FontHelper

class ProfilerPanel {
  fun render(sb: SpriteBatch) {
    if (Settings.isDebug) {
      sb.color = Color.WHITE
      FontHelper.cardTitleFont.data.setScale(.65f)

      FontHelper.renderFontRightAligned(
        sb,
        FontHelper.cardTitleFont,
        "=== Method Profiler - use Annotation @ProfileMethod ===",
        Settings.WIDTH.minus(30f * Settings.scale),
        Settings.HEIGHT.div(5f).times(4),
        Color.WHITE
      )

      SimpleProfilerPatches.profiledMethods.entries.forEachIndexed { index, it ->
        FontHelper.renderFontRightAligned(
          sb,
          FontHelper.cardTitleFont,
          "${it.key} | ~${it.value.getAverageMs()}ms",
          Settings.WIDTH.minus(30f * Settings.scale),
          Settings.HEIGHT.div(5f).times(4).minus(16f * Settings.scale * (index + 1)),
          Color.WHITE
        )
      }
    }
  }
}