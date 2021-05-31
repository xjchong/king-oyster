package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position

data class DamageEvent(
    val position: Position,
    val source: Entity,
    val target: Entity,
    val value: Int
) : Event