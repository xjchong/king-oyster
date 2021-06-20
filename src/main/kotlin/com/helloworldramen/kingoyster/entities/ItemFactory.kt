package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.Heal
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.*
import com.helloworldramen.kingoyster.parts.combat.statuseffects.BurnStatusEffect
import com.helloworldramen.kingoyster.parts.combat.statuseffects.PoisonStatusEffect
import com.helloworldramen.kingoyster.parts.combat.statuseffects.StatusEffect
import godot.core.Color
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

object ItemFactory {

    fun medicine(): EntityFactoryFn = {
        Entity(
            name = "medicine",
            parts = listOf(
                AppearancePart(
                    description = "Restores HP and cures sickness. Restoration increases as HP decreases.",
                    ascii = '!',
                    color = Color.mediumseagreen,
                    sprite = "medicine"
                ),
                ItemPart(
                    uses = 1,
                    effect = { context, user ->
                        val maxHealth = user.maxHealth()

                        if (maxHealth <= 0) {
                            return@ItemPart false
                        }

                        // Medicine heals more when the user is low on health.
                        val healthPercent = user.health() / maxHealth.toDouble()
                        val maxPotencyThreshold = 0.3
                        val maxPotency = 0.3 * maxHealth
                        val potencyPercent = (-1 * (healthPercent)) + (1 + maxPotencyThreshold)

                        user.statusEffects().forEach {
                            if (it is PoisonStatusEffect) {
                                it.turnsRemaining = 0
                            }
                        }

                        user.respondToAction(Heal(context, user, (maxPotency * potencyPercent).roundToInt()))

                        true
                    }
                )
            )
        )
    }

    fun scrollOfFire(): EntityFactoryFn = {
        Entity(
            name = "fire scroll",
            parts = listOf(
                AppearancePart(
                    description = "Casts beams of fire in all directions.",
                    ascii = '?',
                    color = Color.red,
                ),
                ItemPart(
                    uses = 1,
                    effect = { context, user ->
                        val userPosition = context.positionOf(user) ?: return@ItemPart false

                        // Find all the positions in each direction until a wall.
                        for (direction in Direction.all()) {
                            val path = context.straightPathUntil(userPosition + direction.vector, direction) { position ->
                                context.entitiesAt(position)?.any {
                                    !it.isPassable() && !it.has<MovementPart>()
                                } != false
                            }

                            path.forEach {
                                context.world.respondToActions(it, Damage(
                                    context = context,
                                    actor = user,
                                    amount = 25,
                                    damageType = DamageType.Magic,
                                    elementType = ElementType.Fire,
                                    statusEffect = BurnStatusEffect(2, 0.5, 5)
                                ))
                            }
                        }

                        true
                    }
                )
            )
        )
    }
}