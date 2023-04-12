package com.blanktheevil.betterdebug.patches

import com.blanktheevil.betterdebug.ProfileMethod
import com.blanktheevil.betterdebug.Profiler
import com.evacipated.cardcrawl.modthespire.Loader
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch
import com.megacrit.cardcrawl.core.CardCrawlGame
import javassist.CtBehavior
import javassist.CtClass
import javassist.CtConstructor
import org.clapper.util.classutil.*
import org.jetbrains.kotlin.utils.doNothing
import java.io.File
import java.net.URISyntaxException
import kotlin.collections.ArrayList

@SpirePatch2(clz = CardCrawlGame::class, method = SpirePatch.CONSTRUCTOR)
object SimpleProfilerPatches {

  internal val profiledMethods = mutableMapOf<String, Profiler>()
  internal var profilerEnabled = false

  @SpireRawPatch
  @JvmStatic
  @Suppress("unused")
  fun profilerPatch(ctBehavior: CtBehavior) {
    val classes = ClassFinder().getAllModClasses()

    try {
      classes
        .convertToCtClassList(ctBehavior)
        .flattenDeclaredBehaviors()
        .annotationFilter()
        .applyPatch()

      profilerEnabled = true
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  @Suppress("unused")
  @JvmStatic
  fun startProfile(methodName: String) {
    if (profiledMethods[methodName] == null) {
      profiledMethods[methodName] = Profiler(System.nanoTime())
    } else {
      profiledMethods[methodName]?.setStartTime()
    }
  }

  @Suppress("unused")
  @JvmStatic
  fun endProfile(methodName: String) {
    profiledMethods[methodName]?.setEndTime()
  }

  private fun List<ClassInfo>.convertToCtClassList(ctBehavior: CtBehavior): List<CtClass> {
    return mapNotNull {
      try {
        ctBehavior.declaringClass.classPool.get(it.className)
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }
  }

  private fun List<CtClass>.flattenDeclaredBehaviors(): List<CtBehavior> {
    return flatMap { it.declaredBehaviors.toList() }
  }

  private fun List<CtBehavior>.annotationFilter(): List<CtBehavior> {
    return filter { it.hasAnnotation(ProfileMethod::class.java) && !it.isEmpty }
  }

  private fun List<CtBehavior>.applyPatch() {
    forEach {
      println("Patching ${it.longName}...")
      try {
        val behaviorName = "${it.declaringClass.simpleName}.${it.name}"
        val prefixCall = "${this::class.java.name}.startProfile(\"$behaviorName\");\n"
        val postfixCall = "${this::class.java.name}.endProfile(\"$behaviorName\");\n"

        it.prefix(prefixCall)
        it.postfix(postfixCall)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  private fun CtBehavior.prefix(src: String) =
    (if (this is CtConstructor) {
      this::insertBeforeBody
    } else {
      this::insertBefore
    })(src)

  private fun CtBehavior.postfix(src: String) =
    this.insertAfter(src)

  private fun ClassFinder.getAllModClasses(): List<ClassInfo> {
    Loader.MODINFOS
      .filter { it.jarURL != null }
      .forEach {
        try {
          add(File(it.jarURL.toURI()))
        } catch (e: URISyntaxException) {
          doNothing()
        }
      }

    return ArrayList<ClassInfo>().also {
      this.findClasses(it, null)
    }
  }
}