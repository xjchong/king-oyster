package com.helloworldramen.kingoyster.worldgen.population.strategies

import com.helloworldramen.kingoyster.entities.ActorFactory
import com.helloworldramen.kingoyster.entities.WeaponFactory
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.rules.StairsPopulationRule

class SmorgasbordPopulationStrategy(densityFactor: Double = 1.0) : PopulationStrategy(
    PopulationTemplate(PopulationRule(ActorFactory.goblin()))
        .withDensity(0.04 * densityFactor),
    PopulationTemplate(PopulationRule(ActorFactory.blueSlime()))
        .withDensity(0.03 * densityFactor),
    PopulationTemplate(PopulationRule(ActorFactory.redSlime()))
        .withDensity(0.01 * densityFactor),
    PopulationTemplate(PopulationRule(ActorFactory.ghost()))
        .withCount(1 * densityFactor, 2 * densityFactor),
    PopulationTemplate(PopulationRule(WeaponFactory.newDagger()))
        .withCount(2 * densityFactor, 4 * densityFactor),
    PopulationTemplate(PopulationRule(WeaponFactory.newLongsword()))
        .withCount(2 * densityFactor, 4 * densityFactor),
    PopulationTemplate(PopulationRule(WeaponFactory.newGreatsword()))
        .withCount(2 * densityFactor, 4 * densityFactor),
    PopulationTemplate(StairsPopulationRule()).withCount(1)
)