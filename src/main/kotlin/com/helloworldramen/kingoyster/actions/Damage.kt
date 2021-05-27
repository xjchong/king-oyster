package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity

data class Damage(
    override val context: Context,
    override val actor: Entity,
    val amount: Int
) : Action {

    override val timeFactor: Double = 0.0
}