package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.*

data class WeaponAttack(
    override val context: Context,
    override val actor: Entity,
    val direction: Direction
) : Action {

    override val world: World = context.world
    override val timeFactor: Double = 1.0
}