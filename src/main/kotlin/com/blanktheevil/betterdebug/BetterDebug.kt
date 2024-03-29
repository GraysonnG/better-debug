package com.blanktheevil.betterdebug

import basemod.BaseMod
import basemod.interfaces.PostInitializeSubscriber
import com.blanktheevil.betterdebug.ui.DebugInfoPanel
import com.blanktheevil.betterdebug.ui.ProfilerPanel
import com.blanktheevil.betterdebug.utils.BetterDebugControls
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer

@SpireInitializer
class BetterDebug : PostInitializeSubscriber{
  companion object {
    lateinit var debugInfoPanel: DebugInfoPanel
    lateinit var profilerPanel: ProfilerPanel
    lateinit var debugControls: BetterDebugControls

    var isPaused = false

    @JvmStatic
    fun initialize() {
      BaseMod.subscribe(BetterDebug())
    }
  }

  override fun receivePostInitialize() {
    debugInfoPanel = DebugInfoPanel()
    debugControls = BetterDebugControls()
    profilerPanel = ProfilerPanel()
  }
}
