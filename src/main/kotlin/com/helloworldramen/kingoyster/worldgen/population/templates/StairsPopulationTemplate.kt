package com.helloworldramen.kingoyster.worldgen.population.templates

import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate
import com.helloworldramen.kingoyster.worldgen.population.rules.StairsPopulationRule

object StairsPopulationTemplate: PopulationTemplate(
    1 to StairsPopulationRule()
) {
    override val minCount: Int = 1
    override val maxCount: Int = 1
}