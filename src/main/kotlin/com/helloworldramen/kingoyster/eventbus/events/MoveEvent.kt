package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.actions.MoveType
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.eventbus.Event

data class MoveEvent(
    val entity: Entity,
    val oldPosition: Position,
    val newPosition: Position,
    val type: MoveType
) : Event