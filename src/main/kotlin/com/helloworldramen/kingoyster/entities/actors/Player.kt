package com.helloworldramen.kingoyster.entities.actors

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.MovementPart
import com.helloworldramen.kingoyster.parts.PhysicalPart

class Player : Entity(
    parts = listOf(
        PhysicalPart(
            isPassable = false
        ),
        MovementPart()
    ),
    requiresUpdate = true,
    requiresInput = true
)