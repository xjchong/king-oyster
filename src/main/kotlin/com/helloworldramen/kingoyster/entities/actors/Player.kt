package com.helloworldramen.kingoyster.entities.actors

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.InventoryPart
import com.helloworldramen.kingoyster.parts.MovementPart
import com.helloworldramen.kingoyster.parts.PhysicalPart
import com.helloworldramen.kingoyster.parts.SensoryPart

class Player : Entity(
    parts = listOf(
        InventoryPart(6),
        PhysicalPart(
            isPassable = false
        ),
        MovementPart(),
        SensoryPart(
            visionRange = 5
        )
    ),
    requiresUpdate = true,
    requiresInput = true
)