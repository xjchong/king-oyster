package com.helloworldramen.kingoyster.entities.actors

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.*

class Player : Entity(
    name = "player",
    parts = listOf(
        InventoryPart(6),
        PhysicalPart(
            isPassable = false
        ),
        MemoryPart(),
        MovementPart(),
        SensoryPart(
            visionRange = 8
        )
    ),
    requiresUpdate = true,
    requiresInput = true
)