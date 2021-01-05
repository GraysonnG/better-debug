package com.blanktheevil.betterdebug

import basemod.BaseMod
import basemod.interfaces.PostInitializeSubscriber
import com.blanktheevil.betterdebug.ui.DebugInfo
import com.blanktheevil.betterdebug.ui.DebugInfoPanel
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer
import com.megacrit.cardcrawl.core.GameCursor
import com.megacrit.cardcrawl.core.Settings
import java.util.function.Supplier

@SpireInitializer
class BetterDebug : PostInitializeSubscriber{
  companion object {
    lateinit var debugInfoPanel: DebugInfoPanel

    var isPaused = false

    @JvmStatic
    fun initialize() {
      BaseMod.subscribe(BetterDebug())
    }
  }

  override fun receivePostInitialize() {
    debugInfoPanel = DebugInfoPanel()
  }
}