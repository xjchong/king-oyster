package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity

data class Open(
    override val context: Context,
    override val actor: Entity
) : Action {

    override val timeCost: Int = 0
}