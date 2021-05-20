package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position

data class DeathEvent(
    val position: Position,
    val entity: Entity
) : Event