package com.helloworldramen.kingoyster.worldgen.population.strategies

import com.helloworldramen.kingoyster.entities.ActorFactory
import com.helloworldramen.kingoyster.entities.WeaponFactory
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.rules.StairsPopulationRule
import com.helloworldramen.kingoyster.worldgen.population.templates.MonoculturePopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.templates.SingletonPopulationTemplate

class SmorgasbordPopulationStrategy : PopulationStrategy() {

    override val templates: List<PopulationTemplate> = listOf(
        MonoculturePopulationTemplate(0.04, PopulationRule({ ActorFactory.goblin() })),
        MonoculturePopulationTemplate(0.04, PopulationRule({ ActorFactory.slime() })),
        MonoculturePopulationTemplate(1, 2, PopulationRule({ ActorFactory.ghost() })),
        MonoculturePopulationTemplate(2, 4, PopulationRule({ WeaponFactory.newDagger() })),
        MonoculturePopulationTemplate(2, 4, PopulationRule({ WeaponFactory.newLongsword() })),
        MonoculturePopulationTemplate(2, 4, PopulationRule({ WeaponFactory.newGreatsword() })),
        SingletonPopulationTemplate(StairsPopulationRule())
    )
}