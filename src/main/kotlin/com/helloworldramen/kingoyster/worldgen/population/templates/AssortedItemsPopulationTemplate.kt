package com.helloworldramen.kingoyster.worldgen.population.templates

import com.helloworldramen.kingoyster.entities.ItemFactory
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate

object AssortedItemsPopulationTemplate : PopulationTemplate(
    5 to PopulationRule(ItemFactory.medicine()),
    10 to PopulationRule(ItemFactory.scrollOfFire()),
    10 to PopulationRule(ItemFactory.scrollOfIce()),
    10 to PopulationRule(ItemFactory.scrollOfVolt()),
    10 to PopulationRule(ItemFactory.scrollOfSickness()),
    10 to PopulationRule(ItemFactory.scrollOfBlink()),
    10 to PopulationRule(ItemFactory.scrollOfBanish()),
)