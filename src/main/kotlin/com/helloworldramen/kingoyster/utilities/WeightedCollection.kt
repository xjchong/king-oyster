package com.helloworldramen.kingoyster.utilities

import kotlin.random.Random

data class WeightedEntry<T: Any>(val weight: Int, val item: T) {

    constructor(weightItemPair: Pair<Int, T>): this(weightItemPair.first, weightItemPair.second)
}

open class WeightedCollection<T: Any>(entries: List<WeightedEntry<T>>) {

    constructor(vararg entryPairs: Pair<Int, T>): this(entryPairs.map { WeightedEntry<T>(it) }.toMutableList())

    protected val entries = entries.toMutableList()

    override fun toString(): String {
        return entries.toString()
    }

    fun sample(): T? {
        return sampleWeightedEntry()?.item
    }

    fun isEmpty(): Boolean {
        return entries.isEmpty()
    }

    protected fun sampleWeightedEntry(): WeightedEntry<T>? {
        if (entries.isEmpty()) return null

        var totalWeight = entries.fold(0) { acc: Int, entry: WeightedEntry<T> ->
            acc + entry.weight
        }

        val roll = Random.nextInt(totalWeight)

        for (entry in entries) {
            totalWeight -= entry.weight

            if (roll >= totalWeight) return entry
        }

        return null
    }
}

class MutableWeightedCollection<T: Any>(entries: List<WeightedEntry<T>>): WeightedCollection<T>(entries) {

    constructor(vararg entryPairs: Pair<Int, T>): this(entryPairs.map { WeightedEntry<T>(it) }.toMutableList())

    fun remove(): T? {
        val entry = sampleWeightedEntry() ?: return null

        if (!entries.remove(entry)) return null

        return entry.item
    }
}
