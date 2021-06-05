package com.helloworldramen.kingoyster.worldgen.population.strategies

import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate

object EmptyPopulationStrategy : PopulationStrategy() {

    override val templates: List<PopulationTemplate> = listOf()
}