package com.blanktheevil.betterdebug.patches

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.helpers.FontHelper
import com.megacrit.cardcrawl.helpers.Hitbox
import com.megacrit.cardcrawl.helpers.ImageMaster

@Suppress("unused", "UNUSED_PARAMETER")
@SpirePatch(clz = Hitbox::class, method = "render")
object HitboxPatch {
  val hbColor: Color by lazy {
    Color(1f,0f,0f,0.2f)
  }

  @SpirePrefixPatch
  @JvmStatic
  fun renderPatch(instance: Hitbox, sb: SpriteBatch): SpireReturn<Void> {
    return if (Settings.isDebug) {
      sb.color = hbColor

      var shouldRenderText = false

      if (instance.hovered) {
        sb.color = Color.LIME.cpy().also { it.a = 0.3f }
        shouldRenderText = true
      }

      sb.draw(ImageMaster.WHITE_SQUARE_IMG, instance.x, instance.y, instance.width, instance.height)

      if (shouldRenderText) {
        val preSize = FontHelper.cardTitleFont.data.scaleX
        FontHelper.cardTitleFont.data.setScale(0.5f)
        FontHelper.renderFontLeftDownAligned(
          sb,
          FontHelper.cardTitleFont,
          "x: ${instance.x}, y: ${instance.y}",
          instance.x,
          instance.y,
          Color.WHITE
        )

        FontHelper.renderFontRightAligned(
          sb,
          FontHelper.cardTitleFont,
          "h: ${instance.height}",
          instance.x,
          instance.cY,
          Color.WHITE
        )

        FontHelper.renderFontCentered(
          sb,
          FontHelper.cardTitleFont,
          "w: ${instance.width}",
          instance.cX,
          instance.y.plus(instance.height),
          Color.WHITE
        )

        FontHelper.cardTitleFont.data.setScale(preSize)
      }

      sb.color = Color.WHITE.cpy()

      SpireReturn.Return(null)
    } else SpireReturn.Continue()
  }
}