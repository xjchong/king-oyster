package com.helloworldramen.kingoyster.worldgen.population.templates

import com.helloworldramen.kingoyster.entities.WeaponFactory
import com.helloworldramen.kingoyster.worldgen.population.PopulationRule
import com.helloworldramen.kingoyster.worldgen.population.PopulationTemplate

object AssortedWeaponsPopulationTemplate: PopulationTemplate(
    100 to PopulationRule(WeaponFactory.dagger()),
    100 to PopulationRule(WeaponFactory.longsword()),
    100 to PopulationRule(WeaponFactory.greatsword()),
    100 to PopulationRule(WeaponFactory.spear()),
    80 to PopulationRule(WeaponFactory.rapier()),
    60 to PopulationRule(WeaponFactory.scythe()),
)