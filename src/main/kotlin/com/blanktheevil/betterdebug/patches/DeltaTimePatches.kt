package com.blanktheevil.betterdebug.patches

import com.badlogic.gdx.Graphics
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics
import com.blanktheevil.betterdebug.BetterDebug
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch

object DeltaTimePatches {
  @SpirePatch(clz = LwjglGraphics::class, method = "getRawDeltaTime")
  object RawDeltaTime {
    @JvmStatic
    @SpirePostfixPatch
    fun pauseOverride(result: Float, instance: Graphics): Float = if (BetterDebug.isPaused) 0f else result
  }

  @SpirePatch(clz = LwjglGraphics::class, method = "getDeltaTime")
  object DeltaTime {
    @JvmStatic
    @SpirePostfixPatch
    fun pauseOverride(result: Float, instance: Graphics): Float = if (BetterDebug.isPaused) 0f else result
  }
}