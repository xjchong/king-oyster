package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event

data class TriggerTrapEvent(
    val trap: Entity,
    val triggerer: Entity
) : Event