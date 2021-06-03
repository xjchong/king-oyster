package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.parts.equippedWeaponPart

data class ThrowWeapon(
    override val context: Context,
    override val actor: Entity,
    val direction: Direction
) : Action {

    override val world: World = context.world
    override val timeFactor: Double
        get() = actor.equippedWeaponPart()?.attackInfo?.timeFactor ?: 1.0
}