package com.helloworldramen.kingoyster.utilities.worldgen.population.strategies

import com.helloworldramen.kingoyster.utilities.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.population.PopulationTemplate

object EmptyPopulationStrategy : PopulationStrategy() {

    override val templates: List<PopulationTemplate> = listOf()
}