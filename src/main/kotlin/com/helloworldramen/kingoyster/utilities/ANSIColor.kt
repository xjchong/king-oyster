package com.helloworldramen.kingoyster.utilities

sealed class ANSIColor(val code: String) {
    object BLACK : ANSIColor("\u001b[30m")
    object BLUE : ANSIColor("\u001b[34m")
    object CYAN : ANSIColor("\u001b[36m")
    object GREEN : ANSIColor("\u001b[32m")
    object MAGENTA : ANSIColor("\u001b[35m")
    object RED : ANSIColor("\u001b[31m")
    object WHITE : ANSIColor("\u001b[37m")
    object YELLOW : ANSIColor("\u001b[33m")
    object BRIGHT_BLACK : ANSIColor("\u001b[30;1m")
    object BRIGHT_BLUE : ANSIColor("\u001b[34;1m")
    object BRIGHT_CYAN : ANSIColor("\u001b[36;1m")
    object BRIGHT_GREEN : ANSIColor("\u001b[32;1m")
    object BRIGHT_MAGENTA : ANSIColor("\u001b[35;1m")
    object BRIGHT_RED : ANSIColor("\u001b[31;1m")
    object BRIGHT_WHITE : ANSIColor("\u001b[37;1m")
    object BRIGHT_YELLOW : ANSIColor("\u001b[33;1m")
    object BG_BLACK : ANSIColor("\u001b[40m")
    object BG_BLUE : ANSIColor("\u001b[44m")
    object BG_CYAN : ANSIColor("\u001b[46m")
    object BG_GREEN : ANSIColor("\u001b[42m")
    object BG_MAGENTA : ANSIColor("\u001b[45m")
    object BG_RED : ANSIColor("\u001b[41m")
    object BG_WHITE : ANSIColor("\u001b[47m")
    object BG_YELLOW : ANSIColor("\u001b[43m")
    object BG_BRIGHT_BLACK : ANSIColor("\u001b[40m")
    object BG_BRIGHT_BLUE : ANSIColor("\u001b[44m")
    object BG_BRIGHT_CYAN : ANSIColor("\u001b[46m")
    object BG_BRIGHT_GREEN : ANSIColor("\u001b[42m")
    object BG_BRIGHT_MAGENTA : ANSIColor("\u001b[45m")
    object BG_BRIGHT_RED : ANSIColor("\u001b[41m")
    object BG_BRIGHT_WHITE : ANSIColor("\u001b[47m")
    object BG_BRIGHT_YELLOW : ANSIColor("\u001b[43m")
}

fun String.color(color: ANSIColor): String {
    return "${color.code}$this\u001b[0m"
}

fun Char.color(color: ANSIColor): String {
    return "${color.code}$this\u001b[0m"
}