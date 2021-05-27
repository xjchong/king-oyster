package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.parts.MoneyPart
import kotlin.random.Random

object ItemFactory {

    fun coin(minValue: Int, maxValue: Int) = Entity(
        name = "coin",
        parts = listOf(
            ItemPart(),
            MoneyPart(Random.nextInt(minValue, maxValue))
        )
    )
}