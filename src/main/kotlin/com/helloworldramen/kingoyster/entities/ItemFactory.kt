package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.Heal
import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.*
import com.helloworldramen.kingoyster.parts.combat.statuseffects.BurnStatusEffect
import com.helloworldramen.kingoyster.parts.combat.statuseffects.PoisonStatusEffect
import com.helloworldramen.kingoyster.parts.MovementPart
import com.helloworldramen.kingoyster.parts.combat.statuseffects.ColdStatusEffect
import com.helloworldramen.kingoyster.utilities.FloodFill
import godot.core.Color
import kotlin.math.roundToInt

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
            name = "fire beams",
            parts = listOf(
                AppearancePart(
                    description = "Casts beams of fire in all directions.",
                    ascii = '?',
                    color = Color.red,
                    sprite = "scrolls",
                    frameIndex = 0
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

                            context.applyAction(path, Damage(
                                context = context,
                                actor = user,
                                amount = 25,
                                damageType = DamageType.Magic,
                                elementType = ElementType.Fire,
                                statusEffect = BurnStatusEffect(2, 0.5, 5)
                            ))
                        }

                        true
                    }
                )
            )
        )
    }

    fun scrollOfIce(): EntityFactoryFn = {
        Entity(
            name = "ice beams",
            parts = listOf(
                AppearancePart(
                    description = "Casts beams of ice in all directions.",
                    ascii = '?',
                    color = Color.cyan,
                    sprite = "scrolls",
                    frameIndex = 1
                ),
                ItemPart(
                    uses = 1,
                    effect = { context, user ->
                        val userPosition = context.positionOf(user) ?: return@ItemPart false

                        // Find all the positions in each direction until a wall.
                        for (direction in Direction.all()) {
                            val path = context.straightPathUntil(userPosition + direction.vector, direction) { position ->
                                context.entitiesAt(position)?.any { it.isBarrier() } != false
                            }

                            context.applyAction(path, Damage(
                                context = context,
                                actor = user,
                                amount = 10,
                                damageType = DamageType.Magic,
                                elementType = ElementType.Ice,
                                statusEffect = ColdStatusEffect(1.0, 10)
                            ))
                        }

                        true
                    }
                )
            )
        )
    }

    fun scrollOfVolt(): EntityFactoryFn = {
        Entity(
            name = "volt beams",
            parts = listOf(
                AppearancePart(
                    description = "Casts beams of volt in all directions",
                    ascii = '?',
                    color = Color.yellow,
                    sprite = "scrolls",
                    frameIndex = 2
                ),
                ItemPart(
                    uses = 1,
                    effect = { context, user ->
                        val userPosition = context.positionOf(user) ?: return@ItemPart false

                        // Find all the positions in each direction until a wall.
                        for (direction in Direction.all()) {
                            val path = context.straightPathUntil(userPosition + direction.vector, direction) { position ->
                                context.entitiesAt(position)?.any { it.isBarrier() } != false
                            }

                            context.applyAction(path, Damage(
                                context = context,
                                actor = user,
                                amount = 40,
                                damageType = DamageType.Magic,
                                elementType = ElementType.Volt
                            ))
                        }

                        true
                    }
                )
            )
        )
    }

    fun scrollOfSickness(): EntityFactoryFn = {
        Entity(
            name = "poison cloud",
            parts = listOf(
                AppearancePart(
                    description = "Casts a cloud of sickness around the user.",
                    ascii = '?',
                    color = Color.purple,
                    sprite = "scrolls",
                    frameIndex = 3
                ),
                ItemPart(
                    uses = 1,
                    effect = { context, user ->
                        val userPosition = context.positionOf(user) ?: return@ItemPart false
                        val cloudSize = 5
                        val cloudPositions = FloodFill.fill(userPosition.x, userPosition.y, cloudSize) { x, y ->
                            context.entitiesAt(Position(x, y))?.any { it.isBarrier() } != false
                        }.map { xy ->
                            Position(xy)
                        }.filter { position ->
                            position != userPosition
                        }

                        cloudPositions.forEach { position ->
                            context.applyAction(position, Damage(
                                context = context,
                                actor = user,
                                amount = 1,
                                damageType = DamageType.Special,
                                elementType = ElementType.Poison,
                                statusEffect = PoisonStatusEffect(7, 1.0, 6)
                            ))
                        }

                        true
                    }
                )
            )
        )
    }

    fun scrollOfBlink(): EntityFactoryFn = {
        Entity(
            name = "blink",
            parts = listOf(
                AppearancePart(
                    description = "Teleports the user to a random location.",
                    ascii = '?',
                    color = Color.blue,
                    sprite = "scrolls",
                    frameIndex = 4
                ),
                ItemPart(
                    uses = 1,
                    effect = { context, user ->
                        val world = context.world
                        val nextPosition = Position(world.width - 1, world.height - 1)
                            .map { it }
                            .filter { context.entitiesAt(it)?.isEmpty() == true }
                            .randomOrNull() ?: return@ItemPart false

                        user.respondToAction(Move(context, user, nextPosition, 0.0))

                        true
                    }
                )
            )
        )
    }

    fun scrollOfBanish(): EntityFactoryFn = {
        Entity(
            name = "banish",
            parts = listOf(
                AppearancePart(
                    description = "Teleports things around the user to random locations.",
                    ascii = '?',
                    color = Color.pink,
                    sprite = "scrolls",
                    frameIndex = 5
                ),
                ItemPart(
                    uses = 1,
                    effect = { context, user ->
                        val world = context.world
                        val emptyPositions = Position(world.width - 1, world.height - 1)
                            .map { it }
                            .filter { context.entitiesAt(it)?.isEmpty() == true }

                        val currentPosition = context.positionOf(user) ?: return@ItemPart false
                        val radius = 5
                        val banishedPositions = FloodFill.fill(currentPosition.x, currentPosition.y, radius) { _, _ ->
                            false
                        }.map {
                            Position(it)
                        }.filter {
                            it != currentPosition
                        }

                        banishedPositions.forEach { position ->
                            val entities = context.entitiesAt(position) ?: listOf()

                            entities.forEach { entity ->
                                val teleportPosition = emptyPositions.randomOrNull() ?: position

                                entity.respondToAction(Move(context, entity, teleportPosition, 0.0))
                            }
                        }

                        true
                    }
                )
            )
        )
    }
}