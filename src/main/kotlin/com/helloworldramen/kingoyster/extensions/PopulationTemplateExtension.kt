package com.helloworldramen.kingoyster.extensions

import com.helloworldramen.kingoyster.entities.EntityTable
import com.helloworldramen.kingoyster.utilities.WeightedEntry
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate

fun PopulationTemplate.toEntityTable(): EntityTable {
    val weightedEntries = rules.map { weight, rule ->
        WeightedEntry(weight, rule.entityFactoryFn)
    }

    return EntityTable(weightedEntries)
}