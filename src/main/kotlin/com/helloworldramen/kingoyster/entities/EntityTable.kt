package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFnNullable
import com.helloworldramen.kingoyster.utilities.WeightedCollection
import com.helloworldramen.kingoyster.utilities.WeightedEntry

class EntityTable(entries: List<WeightedEntry<EntityFactoryFnNullable>>) {

    constructor(vararg entries: WeightedEntry<EntityFactoryFnNullable>): this(entries.toList())

    constructor(vararg entryPairs: Pair<Int, EntityFactoryFnNullable>): this(entryPairs.map { WeightedEntry(it) })

    private val collection: WeightedCollection<EntityFactoryFnNullable> = WeightedCollection(
        entries
    )

    fun generate(): Entity? {
        return collection.sample()?.invoke()
    }
}