package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.actions.Heal
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.AppearancePart
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.parts.MoneyPart
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import godot.core.Color
import kotlin.random.Random

object ItemFactory {

    fun medicine(): EntityFactoryFn = {
        Entity(
            name = "medicine",
            parts = listOf(
                AppearancePart(
                    ascii = '!',
                    color = Color.mediumseagreen
                ),
                ItemPart(
                    uses = 1,
                    effect = { context, user ->
                        user.respondToAction(Heal(context, user, 10))

                        true
                    }
                )
            )
        )
    }
}