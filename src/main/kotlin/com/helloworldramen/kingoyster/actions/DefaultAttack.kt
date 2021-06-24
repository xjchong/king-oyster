package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.*

data class DefaultAttack(
    override val context: Context,
    override val actor: Entity,
    val direction: Direction
) : Action {

    override val world: World = context.world
    override val timeFactor: Double = 1.0
}