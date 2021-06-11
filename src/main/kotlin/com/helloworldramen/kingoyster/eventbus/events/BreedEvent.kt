package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event

data class BreedEvent(
    val breeder: Entity,
    val child: Entity
) : Event
