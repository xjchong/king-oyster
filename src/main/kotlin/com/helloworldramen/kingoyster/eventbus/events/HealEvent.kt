package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event

data class HealEvent(
    val source: Entity,
    val target: Entity,
    val amount: Int
) : Event