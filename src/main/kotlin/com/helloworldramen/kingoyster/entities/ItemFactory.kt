package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.parts.MoneyPart
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import kotlin.random.Random

object ItemFactory {

    fun medicine() = Entity(
        name = "medicine",
        parts = listOf(
            ItemPart(
                uses = 1,
                effect = { context, user ->
                    user.find<CombatPart>()?.modifyHealth(context, user, 10)

                    true
                }
            )
        )
    )
}