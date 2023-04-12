package com.blanktheevil.betterdebug.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.blanktheevil.betterdebug.BetterDebug
import com.blanktheevil.betterdebug.patches.HitboxPatch
import com.blanktheevil.betterdebug.patches.InputActionPatch
import com.blanktheevil.betterdebug.ui.DebugInfoPanel
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.helpers.input.InputAction

class BetterDebugControls {

  private val toggleDebug = InputAction(Input.Keys.D)
  private val togglePause = InputAction(Input.Keys.P)
  private val toggleHBTexture = InputAction(Input.Keys.H)
  val stepForward = InputAction(Input.Keys.PERIOD)
  private val shiftL = InputAction(Input.Keys.SHIFT_LEFT)
  private val shiftR = InputAction(Input.Keys.SHIFT_RIGHT)
  private val shiftDown: Boolean get() = shiftL.isPressed || shiftR.isPressed

  private var shouldBePaused = false
  private var advanceTimer = 0f
  
  fun update() = if (shiftDown) debugControlsUpdate() else {}

  private fun debugControlsUpdate() {
    InputActionPatch.suppressedInputs.clear()
    DebugInfoPanel.addLines(
      "Toggle Debug - D",
      "Pause Game - P",
      "Step Forward - PERIOD",
      "Swap Hitbox Texture - H"
    )

    if (toggleDebug.isJustPressed) {
      Settings.isDebug = !Settings.isDebug
      InputActionPatch.suppressedInputs.add(Input.Keys.D)
    }

    if (toggleHBTexture.isJustPressed) {
      HitboxPatch.showAltTexture = !HitboxPatch.showAltTexture
      InputActionPatch.suppressedInputs.add(Input.Keys.H)
    }

    if (shouldBePaused) {
      advanceTimer -= Gdx.graphics.rawDeltaTime
      if (advanceTimer < 0) {
        advanceTimer = 0f
        BetterDebug.isPaused = true
      }
    }

    if (stepForward.isJustPressed && advanceTimer <= 0) {
      if (!BetterDebug.isPaused) {
        shouldBePaused = true
        BetterDebug.isPaused = true
      } else {
        advanceTimer = Gdx.graphics.rawDeltaTime * 2f
        BetterDebug.isPaused = false
      }
    }

    if (togglePause.isJustPressed && advanceTimer <= 0f) {
      BetterDebug.isPaused = !BetterDebug.isPaused
      shouldBePaused = BetterDebug.isPaused
    }
  }
}