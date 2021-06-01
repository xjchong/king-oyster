package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.eventbus.Event

data class ThrowWeaponEvent(
    val thrower: Entity,
    val weapon: Entity,
    val from: Position,
    val to: Position,
    val willBreak: Boolean
) : Event