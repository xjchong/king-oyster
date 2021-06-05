package com.helloworldramen.kingoyster.utilities.worldgen.population

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.World

abstract class PopulationStrategy {

    abstract val templates: List<PopulationTemplate>

    fun populate(world: World, player: Entity) {
        templates.forEach { template ->
            template.execute(world, player)
        }
    }
}