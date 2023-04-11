package com.blanktheevil.betterdebug.patches

import com.blanktheevil.betterdebug.ProfileMethod
import com.evacipated.cardcrawl.modthespire.Loader
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch
import com.megacrit.cardcrawl.core.CardCrawlGame
import javassist.CtBehavior
import org.clapper.util.classutil.*
import org.jetbrains.kotlin.utils.doNothing
import java.io.File
import java.math.RoundingMode
import java.net.URISyntaxException
import java.text.DecimalFormat

@SpirePatch2(clz = CardCrawlGame::class, method = SpirePatch.CONSTRUCTOR)
object SimpleProfilerPatches {

  internal val profiledMethods = mutableMapOf<String, Profiler>()

  @SpireRawPatch
  @JvmStatic
  @Suppress("unused")
  fun profilerPatch(ctBehavior: CtBehavior) {
    val classes = ClassFinder().getAllSTSClasses()

    classes
      .map {
        ctBehavior.declaringClass.classPool.get(it.className)
      }
      .flatMap {
        it.declaredMethods.toMutableList()
      }
      .filter {
        it.hasAnnotation(ProfileMethod::class.java)
      }
      .forEach {
        val methodName = "${it.declaringClass.simpleName}.${it.name}"

        val prefixCall = "${this::class.java.name}.startProfile(\"$methodName\");\n"
        val postfixCall = "${this::class.java.name}.endProfile(\"$methodName\");\n"

        println(prefixCall)
        println(postfixCall)

        it.insertBefore(prefixCall)
        it.insertAfter(postfixCall)
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

  class Profiler(
    private var startTimeNs: Long,
  ) {
    private var endTimeNs: Long = startTimeNs
    private val calls = mutableListOf<Long>()

    fun setStartTime() {
      startTimeNs = System.nanoTime()
    }

    fun setEndTime() {
      endTimeNs = System.nanoTime()
      calls.add(endTimeNs.minus(startTimeNs))
      if (calls.size > 120) calls.removeFirstOrNull()
    }

    @Suppress("unused")
    fun getElapsedNanoSeconds(): Long {
      return endTimeNs.minus(startTimeNs)
    }

    fun getAverageMs(): String {
      val format = DecimalFormat("#.##")
      format.roundingMode = RoundingMode.CEILING

      return if (calls.isEmpty()) {
        "???"
      } else {
        format.format(calls.average() / 1000.0)
      }
    }
  }

  fun ClassFinder.getAllSTSClasses(): ArrayList<ClassInfo> {
    val filter = AndClassFilter(
      NotClassFilter(InterfaceOnlyClassFilter()),
      NotClassFilter(AbstractClassFilter()),
    )

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
      this.findClasses(it, filter)
    }
  }
}