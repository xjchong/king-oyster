package com.helloworldramen.kingoyster.utilities.worldgen.population.templates

import com.helloworldramen.kingoyster.utilities.WeightedCollection
import com.helloworldramen.kingoyster.utilities.WeightedEntry
import com.helloworldramen.kingoyster.utilities.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.utilities.worldgen.population.PopulationTemplate

class SingletonPopulationTemplate(rule: PopulationRule) : PopulationTemplate(
    1,
    1,
    WeightedCollection(WeightedEntry(1, rule))
)