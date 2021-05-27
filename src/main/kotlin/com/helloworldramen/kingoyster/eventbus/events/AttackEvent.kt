package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.architecture.Entity

data class AttackEvent(
    val attacker: Entity,
    val target: Entity
) : Event