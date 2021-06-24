package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.eventbus.Event

data class MoveAttackEvent(
    val attacker: Entity,
    val direction: Direction,
    val from: Position,
    val to: Position?
) : Event