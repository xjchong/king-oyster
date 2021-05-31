package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity

data class DropWeapon(
    override val context: Context,
    override val actor: Entity
) : Action {

    override val timeFactor: Double = 0.0
}