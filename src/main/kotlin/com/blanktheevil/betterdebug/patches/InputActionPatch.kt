package com.blanktheevil.betterdebug.patches

import basemod.ReflectionHacks
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn
import com.megacrit.cardcrawl.helpers.input.InputAction

@SpirePatch(clz = InputAction::class, method = "isJustPressed")
object InputActionPatch {
  val suppressedInputs = mutableListOf<Int>()

  @SpirePostfixPatch
  @JvmStatic
  fun suppressInput(result: Boolean, inputAction: InputAction): Boolean {
    val keycode = ReflectionHacks.getPrivate<Int>(inputAction, InputAction::class.java, "keycode")


    suppressedInputs.forEach {
      if (it == keycode) {
        return false
      }
    }

    return result
  }
}