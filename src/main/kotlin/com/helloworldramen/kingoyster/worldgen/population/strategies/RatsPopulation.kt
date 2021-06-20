package com.helloworldramen.kingoyster.worldgen.population.strategies

import com.helloworldramen.kingoyster.entities.ActorFactory
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.templates.AssortedItemsPopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.templates.AssortedWeaponsPopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.templates.StairsPopulationTemplate

object RatsPopulation : PopulationStrategy(
    PopulationTemplate(
        100 to PopulationRule(ActorFactory.giantRat()),
        50 to PopulationRule(ActorFactory.goblin()),
        10 to PopulationRule(ActorFactory.hobgoblin()),
    ).withDensity(0.1),
    AssortedWeaponsPopulationTemplate.withCount(6, 8),
    AssortedItemsPopulationTemplate.withCount(3, 5),
    StairsPopulationTemplate
)