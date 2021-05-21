package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity

data class Take(
    override val context: Context,
    override val actor: Entity
) : Action {

    override val timeFactor: Double = 1.0
}
