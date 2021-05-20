package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position

data class Move(
    override val context: Context,
    override val actor: Entity,
    val position: Position
) : Action {

    override val timeCost: Int = 100
}
