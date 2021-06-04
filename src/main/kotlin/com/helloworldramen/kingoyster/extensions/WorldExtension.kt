package com.helloworldramen.kingoyster.extensions

import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World

fun World.positionsForEach(block: (Position) -> Unit) {
   Position(width - 1, height - 1).forEach(block)
}