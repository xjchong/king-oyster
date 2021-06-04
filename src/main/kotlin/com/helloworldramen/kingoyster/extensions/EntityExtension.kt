package com.helloworldramen.kingoyster.extensions

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.visiblePositions

val Entity.isPlayer: Boolean
    get() = name == "player"

fun Entity.isVisibleToPlayer(context: Context): Boolean {
    with(context) {
        return player.visiblePositions().contains(positionOf(this@isVisibleToPlayer))
    }
}

fun Position.isVisibleToPlayer(context: Context): Boolean {
    with(context) {
        return player.visiblePositions().contains(this@isVisibleToPlayer)
    }
}