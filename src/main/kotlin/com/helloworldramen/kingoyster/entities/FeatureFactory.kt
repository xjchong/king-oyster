package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.Heal
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.utilities.WeightedCollection
import godot.core.Color
import godot.core.Vector2

object FeatureFactory {

    fun chest(): EntityFactoryFn = {
        Entity(
            name = "chest",
            parts = listOf(
                AppearancePart(
                    ascii = '~',
                    color = Color.brown,
                    sprite = "chests",
                    frameIndex = 0
                ),
                CombatPart(5),
                ItemSlotPart(
                    item = EntityTable(
                        100 to ItemFactory.medicine()
                    ).generate()
                ),
                OpenablePart(),
                PhysicalPart(isPassable = true)
            )
        )
    }

    fun door(isHorizontal: Boolean): EntityFactoryFn = {
        Entity(
            name = "door",
            parts = listOf(
                AppearancePart(
                    ascii = '+',
                    color = Color.orange,
                    sprite = if (isHorizontal) "door_horizontal" else "door_vertical",
                    offset = Vector2(0, if (isHorizontal) 2 else 8)
                ),
                CombatPart(20),
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

    fun wall(sprite: String): EntityFactoryFn = {
        Entity(
            name = "wall",
            parts = listOf(
                AppearancePart(
                    ascii = '#',
                    color = Color.white,
                    sprite = sprite,
                    offset = Vector2(0, 8),
                    weightedFrameIndices = WeightedCollection(
                        500 to 0, 50 to 1, 50 to 2,
                        50 to 3, 50 to 4, 50 to 5,
                        50 to 6, 50 to 7, 50 to 8,
                        50 to 9, 50 to 10, 50 to 11
                    )
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
                    sprite = "blue_puddle",
                    offset = Vector2(0, -2)
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