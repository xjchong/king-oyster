package com.helloworldramen.kingoyster.entities.items

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.parts.MoneyPart
import kotlin.random.Random

class Coin private constructor(value: Int) : Entity(
    ItemPart,
    MoneyPart(value = value)
) {

    companion object {
        fun new(minValue: Int, maxValue: Int): Coin {
            return Coin(Random.nextInt(minValue, maxValue))
        }
    }
}