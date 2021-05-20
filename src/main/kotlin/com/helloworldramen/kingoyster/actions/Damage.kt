package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity

data class Damage(
    override val context: Context,
    override val actor: Entity,
    val amount: Int
) : Action {

    override val timeCost: Int = 0
}