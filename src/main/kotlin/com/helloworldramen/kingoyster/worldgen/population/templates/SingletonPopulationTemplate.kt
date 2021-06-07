package com.helloworldramen.kingoyster.worldgen.population.templates

import com.helloworldramen.kingoyster.utilities.WeightedCollection
import com.helloworldramen.kingoyster.utilities.WeightedEntry
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate

class SingletonPopulationTemplate(rule: PopulationRule) : PopulationTemplate(
    1,
    1,
    WeightedCollection(1 to rule)
)