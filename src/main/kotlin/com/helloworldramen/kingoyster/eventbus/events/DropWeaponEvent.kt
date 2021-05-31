package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event

data class DropWeaponEvent(
    val dropper: Entity,
    val weapon: Entity
) : Event
