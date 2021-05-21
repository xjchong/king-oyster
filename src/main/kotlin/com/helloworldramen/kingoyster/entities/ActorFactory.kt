package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.*

object ActorFactory {

    fun player() = Entity(
        name = "player",
        parts = listOf(
            AttackablePart(),
            CombatPart(
                attackPotency = 1
            ),
            HealthPart(
                maxHealth = 10
            ),
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
        timeFactor = 1.0,
        requiresInput = true
    )

    fun slime() = Entity(
        name = "slime",
        parts = listOf(
            AIPart(),
            AttackablePart(),
            CombatPart(
                attackPotency = 1
            ),
            HealthPart(
                maxHealth = 2
            ),
            PhysicalPart(
                isPassable = false
            ),
            MovementPart(),
            SensoryPart(
                visionRange = 4
            )
        ),
        timeFactor = 1.0
    )
}