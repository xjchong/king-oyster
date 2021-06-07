package com.helloworldramen.kingoyster.worldgen.population

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.World

open class PopulationStrategy(private val templates: List<PopulationTemplate>) {

    constructor(vararg templates: PopulationTemplate): this(templates.toList())

    fun populate(world: World, player: Entity) {
        templates.forEach { template ->
            template.execute(world, player)
        }
    }
}