package com.blanktheevil.betterdebug.patches

import com.blanktheevil.betterdebug.BetterDebug
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn
import com.megacrit.cardcrawl.dungeons.AbstractDungeon

@Suppress("unused", "UNUSED_PARAMETER")
@SpirePatch(clz = AbstractDungeon::class, method = "update")
object UpdatePatch {
  @SpirePrefixPatch
  @JvmStatic
  fun updateDebugInfo(dungeon: AbstractDungeon): SpireReturn<Void> {
    BetterDebug.debugInfoPanel.update()

    return SpireReturn.Continue()
  }
}