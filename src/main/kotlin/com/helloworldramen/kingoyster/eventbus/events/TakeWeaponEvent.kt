package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event

data class TakeWeaponEvent(
    val taker: Entity,
    val weapon: Entity
) : Event