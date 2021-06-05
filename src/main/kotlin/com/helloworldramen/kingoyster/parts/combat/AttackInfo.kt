package com.helloworldramen.kingoyster.parts.combat

data class AttackInfo(
    val damageInfo: DamageInfo = DamageInfo(),
    val timeFactor: Double = 1.0
) {

    val powerFactor = damageInfo.powerFactor
    val damageType = damageInfo.damageType
    val elementType = damageInfo.elementType
}