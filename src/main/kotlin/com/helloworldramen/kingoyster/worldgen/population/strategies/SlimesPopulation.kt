package com.helloworldramen.kingoyster.worldgen.population.strategies

import com.helloworldramen.kingoyster.entities.ActorFactory
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.templates.AssortedWeaponsPopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.templates.StairsPopulationTemplate


class SlimesPopulation: PopulationStrategy(
    PopulationTemplate(
        70 to PopulationRule(ActorFactory.blueSlime()),
        30 to PopulationRule(ActorFactory.redSlime()),
    ).withDensity(0.1),
    AssortedWeaponsPopulationTemplate.withCount(6, 8),
    StairsPopulationTemplate
)