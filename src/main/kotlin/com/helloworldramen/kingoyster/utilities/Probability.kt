package com.helloworldramen.kingoyster.utilities

class Probability(value: Double) {

    constructor(value: Int): this(value / 100.0)

    val double: Double = value.coerceIn(0.0, 1.0)
}

fun Double.percentChance(): Probability {
    return Probability(this)
}

fun Int.percentChance(): Probability {
    return Probability(this)
}