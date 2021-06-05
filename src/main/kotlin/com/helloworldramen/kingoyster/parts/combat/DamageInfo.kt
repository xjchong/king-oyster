package com.helloworldramen.kingoyster.parts.combat

data class DamageInfo(
    val powerFactor: Double = 1.0,
    val damageType: DamageType = DamageType.Special,
    val elementType: ElementType = ElementType.None
)

sealed class DamageType {
    object Cut : DamageType()
    object Bash : DamageType()
    object Stab : DamageType()
    object Special : DamageType()
}

sealed class ElementType {
    object Fire : ElementType()
    object Ice : ElementType()
    object Volt : ElementType()
    object None : ElementType()
}