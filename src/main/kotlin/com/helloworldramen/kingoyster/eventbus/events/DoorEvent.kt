package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event

data class DoorEvent(
    val door: Entity,
    val actor: Entity,
    val isOpen: Boolean
) : Event