package com.blanktheevil.betterdebug.patches

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.blanktheevil.betterdebug.BetterDebug
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch
import com.megacrit.cardcrawl.dungeons.AbstractDungeon

@Suppress("unused", "UNUSED_PARAMETER")
@SpirePatch(clz = AbstractDungeon::class, method = "render")
object RenderPatch {
  @SpirePostfixPatch
  @JvmStatic
  fun renderDebugInfo(dungeon: AbstractDungeon, sb: SpriteBatch) {
    BetterDebug.debugInfoPanel.render(sb)
    BetterDebug.debugInfoPanel.renderCursorInfo(sb)
  }
}