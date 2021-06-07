package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.Heal
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.AscendablePart
import com.helloworldramen.kingoyster.parts.PhysicalPart
import com.helloworldramen.kingoyster.parts.OpenablePart
import com.helloworldramen.kingoyster.parts.TrapPart
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType

object FeatureFactory {

    fun door(): EntityFactoryFn = {
        Entity(
            name = "door",
            parts = listOf(
                PhysicalPart(
                    isPassable = false,
                    doesBlockVision = true
                ),
                OpenablePart()
            )
        )
    }

    fun stairs(): EntityFactoryFn = {
        Entity(
            name = "stairs",
            parts = listOf(
                AscendablePart()
            )
        )
    }

    fun wall(): EntityFactoryFn = {
        Entity(
            name = "wall",
            parts = listOf(
                PhysicalPart(
                    isPassable = false,
                    doesBlockVision = true
                )
            )
        )
    }

    fun healingPuddle(): EntityFactoryFn = {
        Entity(
            name = "healing puddle",
            parts = listOf(
                TrapPart({ context, entity ->
                    entity.respondToAction(Heal(context, entity, 5))

                    true
                })
            )
        )
    }

    fun firePuddle(): EntityFactoryFn = {
        Entity(
            name = "fire puddle",
            parts = listOf(
                TrapPart({ context, entity ->
                    entity.respondToAction(
                        Damage(context, Entity.UNKNOWN, 10,
                            damageType = DamageType.Special,
                            elementType = ElementType.Fire
                        ))

                    true
                })
            )
        )
    }
}