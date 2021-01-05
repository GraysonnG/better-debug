package com.blanktheevil.betterdebug.ui

abstract class DebugInfo(val title: String, val delimiter: String = "") {
  abstract fun updateValue(): String
}