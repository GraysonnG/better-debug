package com.blanktheevil.betterdebug.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.blanktheevil.betterdebug.BetterDebug
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.FontHelper
import com.megacrit.cardcrawl.helpers.input.InputHelper
import com.megacrit.cardcrawl.rooms.AbstractRoom
import java.util.function.Supplier

class DebugInfoPanel {
  companion object {
    @JvmStatic
    fun addLine(debugInfo: DebugInfo) {
      BetterDebug.debugInfoPanel.add(debugInfo)
    }

    @JvmStatic
    fun addDebugInfo(title: String, supplier: Supplier<String>) {
      addLine(object : DebugInfo(title, "|") {
        override fun updateValue(): String = supplier.get()
      })
    }

    @JvmStatic
    fun addTitleLine(title: String) {
      addLine(object : DebugInfo(title, "") {
        override fun updateValue(): String = ""
      })
    }

    @JvmStatic
    fun addEmptyLine() {
      addLine(object: DebugInfo("", "") {
        override fun updateValue(): String = ""
      })
    }
  }

  private val infos = mutableListOf<DebugInfo>()
  private val frametimeHistory = mutableListOf<Float>()

  private fun add(debugInfo: DebugInfo) {
    infos.add(debugInfo)
  }

  fun update() {
    addDefaults()
  }

  fun render(sb: SpriteBatch) {
    if (Settings.isDebug) {
      sb.color = Color.WHITE
      FontHelper.cardTitleFont.data.setScale(.65f)

      infos.forEachIndexed { index, it ->
        val title = it.title
        val body = try {
          it.updateValue()
        } catch (e: Exception) {
          "ERROR"
        }
        val del = it.delimiter

        FontHelper.renderFont(
          sb,
          FontHelper.cardTitleFont,
          "$title $del $body",
          30f * Settings.scale,
          Settings.HEIGHT.div(5f).times(4).minus(16f * Settings.scale * index),
          Color.WHITE
        )
      }

    }

    this.infos.clear()
  }

  fun renderCursorInfo(sb: SpriteBatch) {
    if (Settings.isDebug) {
      FontHelper.renderFontLeft(
        sb,
        FontHelper.cardTitleFont,
        "x: ${InputHelper.mX}, y: ${InputHelper.mY}",
        InputHelper.mX.toFloat(),
        InputHelper.mY.toFloat() + 20f.times(Settings.scale),
        Color.WHITE
      )
    }
  }

  private fun addDefaults() {
    addTitleLine("=== DEBUG INFO ===")
    addDebugInfo("resolution") { "${Settings.WIDTH}x${Settings.HEIGHT}" }
    addDebugInfo("scale") { "${Settings.scale}" }
    addDebugInfo("framerate") { "${getCurrentFramerate()}/${Settings.MAX_FPS}" }
    addDebugInfo("paused") { "${BetterDebug.isPaused}" }

    addEmptyLine()

    if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
      addTitleLine("=== ActionManager Visualizer ===")
      if (AbstractDungeon.actionManager.currentAction != null) {
        addTitleLine(">>> ${AbstractDungeon.actionManager.currentAction::class.java.simpleName}")
      }

      AbstractDungeon.actionManager.actions.forEach {
        addTitleLine(it::class.java.simpleName)
      }

      addTitleLine("==============================")
      addEmptyLine()
    }
  }

  private fun getCurrentFramerate(): Float {
    frametimeHistory.add(Gdx.graphics.rawDeltaTime)
    if (frametimeHistory.size > Settings.MAX_FPS) frametimeHistory.removeAt(0)

    val averageFrametime = frametimeHistory.average().toFloat()
    val percentFPS = averageFrametime.times(Settings.MAX_FPS)

    return percentFPS.times(Settings.MAX_FPS)
  }
}