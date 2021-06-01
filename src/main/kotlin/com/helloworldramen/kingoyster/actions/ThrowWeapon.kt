package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.parts.equippedWeaponPart

data class ThrowWeapon(
    override val context: Context,
    override val actor: Entity,
    val direction: Direction
) : Action {

    // Throwing a weapon takes longer than swinging it.
    override val timeFactor: Double
        get() = actor.equippedWeaponPart()?.attackInfo?.timeFactor ?: 1.0 * 1.5
}