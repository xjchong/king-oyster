package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position

data class DeathEvent(
    val position: Position,
    val entity: Entity
) : Event