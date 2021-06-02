package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event

data class DamageWeaponEvent(
    val weapon: Entity,
    val owner: Entity?,
    val amount: Int,
    val isBroken: Boolean
) : Event