package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.actions.Heal
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.AppearancePart
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.parts.MoneyPart
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import com.helloworldramen.kingoyster.parts.combat.health
import com.helloworldramen.kingoyster.parts.combat.maxHealth
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

                        user.respondToAction(Heal(context, user, (maxPotency * potencyPercent).roundToInt()))

                        true
                    }
                )
            )
        )
    }
}