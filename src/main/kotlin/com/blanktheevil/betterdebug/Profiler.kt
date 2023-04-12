package com.blanktheevil.betterdebug

import java.math.RoundingMode
import java.text.DecimalFormat

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