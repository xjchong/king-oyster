package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.parts.equippedWeaponPart

data class WeaponAttack(
    override val context: Context,
    override val actor: Entity
) : Action {

    override val world: World = context.world
    override val timeFactor: Double = 1.0
}