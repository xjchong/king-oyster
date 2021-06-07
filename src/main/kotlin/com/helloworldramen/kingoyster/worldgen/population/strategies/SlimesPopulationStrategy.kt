package com.helloworldramen.kingoyster.worldgen.population.strategies

import com.helloworldramen.kingoyster.entities.ActorFactory
import com.helloworldramen.kingoyster.entities.WeaponFactory
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.rules.StairsPopulationRule


class SlimesPopulationStrategy: PopulationStrategy(
    PopulationTemplate(
        70 to PopulationRule(ActorFactory.blueSlime()),
        30 to PopulationRule(ActorFactory.redSlime()),
    ).withDensity(0.1),
    PopulationTemplate(
        100 to PopulationRule(WeaponFactory.newDagger()),
        100 to PopulationRule(WeaponFactory.newLongsword()),
        100 to PopulationRule(WeaponFactory.newGreatsword()),
    ).withCount(6, 12),
    PopulationTemplate(StairsPopulationRule())
)