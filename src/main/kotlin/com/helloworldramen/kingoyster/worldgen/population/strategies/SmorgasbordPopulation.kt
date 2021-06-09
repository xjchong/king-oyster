package com.helloworldramen.kingoyster.worldgen.population.strategies

import com.helloworldramen.kingoyster.entities.ActorFactory
import com.helloworldramen.kingoyster.entities.FeatureFactory
import com.helloworldramen.kingoyster.entities.ItemFactory
import com.helloworldramen.kingoyster.entities.WeaponFactory
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.rules.StairsPopulationRule

class SmorgasbordPopulation(densityFactor: Double = 1.0) : PopulationStrategy(
    PopulationTemplate(
        100 to PopulationRule(ActorFactory.goblin()),
        70 to PopulationRule(ActorFactory.blueSlime()),
        30 to PopulationRule(ActorFactory.redSlime()),
    ).withDensity(0.1 * densityFactor),
    PopulationTemplate(PopulationRule(ActorFactory.ghost()))
        .withCount(1 * densityFactor, 2 * densityFactor),
    PopulationTemplate(
        100 to PopulationRule(WeaponFactory.dagger()),
        100 to PopulationRule(WeaponFactory.longsword()),
        100 to PopulationRule(WeaponFactory.greatsword()),
        100 to PopulationRule(WeaponFactory.rapier()),
        100 to PopulationRule(WeaponFactory.scythe()),
    ).withCount(8 * densityFactor, 16 * densityFactor),
    PopulationTemplate(
        100 to PopulationRule(ItemFactory.medicine()),
    ).withCount(0 * densityFactor, 2 * densityFactor),
    PopulationTemplate(
        100 to PopulationRule(FeatureFactory.chest()),
    ).withCount(0 * densityFactor, 2 * densityFactor),
    PopulationTemplate(StairsPopulationRule()).withCount(1)
)