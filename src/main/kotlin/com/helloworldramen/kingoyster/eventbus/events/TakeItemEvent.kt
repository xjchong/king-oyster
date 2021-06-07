package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event

data class TakeItemEvent(
    val taker: Entity,
    val item: Entity
) : Event