package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.*

data class Move(
    override val context: Context,
    override val actor: Entity,
    val position: Position,
    val type: MoveType = MoveType.Default
) : Action {

    override val world: World = context.world
    override val timeFactor: Double = 1.0
}

sealed class MoveType {
    object Default : MoveType()
    object Charge : MoveType()
}
