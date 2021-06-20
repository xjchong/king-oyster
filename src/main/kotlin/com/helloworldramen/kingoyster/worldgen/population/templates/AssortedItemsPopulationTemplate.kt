package com.helloworldramen.kingoyster.worldgen.population.templates

import com.helloworldramen.kingoyster.entities.ItemFactory
import com.helloworldramen.kingoyster.entities.WeaponFactory
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate

object AssortedItemsPopulationTemplate : PopulationTemplate(
    10 to PopulationRule(ItemFactory.scrollOfFire())
)