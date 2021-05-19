package com.helloworldramen.kingoyster.entities.items

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.Money
import kotlin.random.Random

class Coin private constructor(value: Int) : Entity(
    Money(value = value)
) {

    companion object {
        fun new(minValue: Int, maxValue: Int): Coin {
            return Coin(Random.nextInt(minValue, maxValue))
        }
    }
}