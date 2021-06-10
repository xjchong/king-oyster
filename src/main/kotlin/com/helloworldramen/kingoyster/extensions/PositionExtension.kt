package com.helloworldramen.kingoyster.extensions

import com.helloworldramen.kingoyster.architecture.Position

fun Pair<Int, Int>.asPosition(): Position {
    return Position(this)
}