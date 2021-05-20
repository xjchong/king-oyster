package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.*

object ActorFactory {

    fun player() = Entity(
        name = "player",
        parts = listOf(
            InventoryPart(6),
            PhysicalPart(
                isPassable = false
            ),
            MemoryPart(),
            MovementPart(),
            SensoryPart(
                visionRange = 10
            )
        ),
        requiresUpdate = true,
        requiresInput = true
    )
}