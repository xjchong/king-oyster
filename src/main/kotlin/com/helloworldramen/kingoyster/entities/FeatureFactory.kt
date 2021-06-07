package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.Heal
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import godot.core.Color
import godot.core.Vector2

object FeatureFactory {

    fun door(): EntityFactoryFn = {
        Entity(
            name = "door",
            parts = listOf(
                AppearancePart(
                    ascii = '+',
                    color = Color.orange
                ),
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
                AppearancePart(
                    ascii = '<',
                    color = Color.white,
                    sprite = "stone_stairs_up"
                ),
                AscendablePart()
            )
        )
    }

    fun wall(): EntityFactoryFn = {
        Entity(
            name = "wall",
            parts = listOf(
                AppearancePart(
                    ascii = '#',
                    color = Color.white,
                    sprite = "grass_stone_wall",
                    offset = Vector2(0, 8)
                ),
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
                AppearancePart(
                    ascii = '^',
                    color = Color.mediumseagreen,
                ),
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
                AppearancePart(
                    ascii = '^',
                    color = Color.red,
                ),
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