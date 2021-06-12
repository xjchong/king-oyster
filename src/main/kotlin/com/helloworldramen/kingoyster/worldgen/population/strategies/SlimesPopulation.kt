package com.helloworldramen.kingoyster.worldgen.population.strategies

import com.helloworldramen.kingoyster.entities.ActorFactory
import com.helloworldramen.kingoyster.entities.WeaponFactory
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.rules.StairsPopulationRule


class SlimesPopulation: PopulationStrategy(
    PopulationTemplate(
        70 to PopulationRule(ActorFactory.blueSlime()),
        30 to PopulationRule(ActorFactory.redSlime()),
    ).withDensity(0.1),
    PopulationTemplate(
        100 to PopulationRule(WeaponFactory.dagger()),
        100 to PopulationRule(WeaponFactory.greatsword()),
        100 to PopulationRule(WeaponFactory.spear()),
        100 to PopulationRule(WeaponFactory.scythe()),
    ).withCount(6, 8),
    PopulationTemplate(StairsPopulationRule())
        .withCount(1)
)