package com.helloworldramen.kingoyster.worldgen.population.templates

import com.helloworldramen.kingoyster.utilities.WeightedCollection
import com.helloworldramen.kingoyster.utilities.WeightedEntry
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate

class MonoculturePopulationTemplate : PopulationTemplate {

    constructor(density: Double, rule: PopulationRule)
            : super(density, weightedCollectionFrom(rule))

    constructor(minCount: Int, maxCount: Int, rule: PopulationRule)
            : super(minCount, maxCount, weightedCollectionFrom(rule))

    companion object {

        private fun weightedCollectionFrom(rule: PopulationRule): WeightedCollection<PopulationRule> {
            return WeightedCollection(WeightedEntry(1, rule))
        }
    }
}