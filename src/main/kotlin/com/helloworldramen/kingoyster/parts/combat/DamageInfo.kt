package com.helloworldramen.kingoyster.parts.combat

import com.helloworldramen.kingoyster.parts.combat.statuseffects.StatusEffect

data class DamageInfo(
    val powerFactor: Double = 0.0,
    val damageType: DamageType = DamageType.Special,
    val elementType: ElementType = ElementType.None,
    val statusEffect: StatusEffect? = null
)

sealed class DamageType {
    object Cut : DamageType()
    object Bash : DamageType()
    object Stab : DamageType()
    object Magic : DamageType()
    object Special : DamageType()
}

sealed class ElementType {
    object Fire : ElementType()
    object Ice : ElementType()
    object Volt : ElementType()
    object Poison : ElementType()
    object None : ElementType()
}