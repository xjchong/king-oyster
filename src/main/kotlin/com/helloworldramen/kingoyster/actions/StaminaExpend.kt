package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.World

data class StaminaExpend(
    override val context: Context,
    override val actor: Entity,
    val amount: Int
) : Action {

    override val world: World = context.world
    override val timeFactor: Double = 0.0
}