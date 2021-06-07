package com.helloworldramen.kingoyster.utilities

import kotlin.random.Random

data class WeightedEntry<T: Any>(val weight: Int, val item: T) {

    constructor(weightItemPair: Pair<Int, T>): this(weightItemPair.first, weightItemPair.second)
}

class WeightedCollection<T: Any>(private val entries: List<WeightedEntry<T>>) {

    constructor(vararg entryPairs: Pair<Int, T>): this(entryPairs.map { WeightedEntry<T>(it) })

    fun sample(): T? {
        if (entries.isEmpty()) return null

        var totalWeight = entries.fold(0) { acc: Int, entry: WeightedEntry<T> ->
            acc + entry.weight
        }

        val roll = Random.nextInt(totalWeight)

        for (entry in entries) {
            totalWeight -= entry.weight

            if (roll >= totalWeight) return entry.item
        }

        return null
    }
}
