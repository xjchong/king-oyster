package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event

data class WeaponAttackEvent(
    val attacker: Entity,
    val direction: Direction,
) : Event