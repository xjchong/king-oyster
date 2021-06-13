package com.helloworldramen.kingoyster.worldgen.population.strategies

import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.rules.StairsPopulationRule

class TestActorPopulation(entityFactoryFn: EntityFactoryFn) : PopulationStrategy(
    PopulationTemplate(1 to PopulationRule(entityFactoryFn)).withCount(1),
    PopulationTemplate(StairsPopulationRule()).withCount(1)
)